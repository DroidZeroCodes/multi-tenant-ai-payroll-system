package org.droid.zero.multitenantaipayrollsystem.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.client.redis.RedisCacheClient;
import org.droid.zero.multitenantaipayrollsystem.security.auth.user.credentials.UserCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${auth.jwt.secret}")
    private String SECRET_KEY;
    @Value("${auth.jwt.expiration.milliseconds}")
    private long JWT_EXPIRATION_MILLIS;

    private final RedisCacheClient redisCacheClient;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

         String roles = authentication.getAuthorities().stream()
             .map(GrantedAuthority::getAuthority)
             .collect(Collectors.joining(","));

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(JWT_EXPIRATION_MILLIS, ChronoUnit.MILLIS)))
                .claim("userId", ((UserCredentials)(authentication.getPrincipal())).getUser().getId())
                .claim("roles", roles)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && !redisCacheClient.isTokenBlacklisted(token)
        );
    }

    public void blacklistToken(String token) {
        long ttl = extractClaim(token, Claims::getExpiration).getTime() - System.currentTimeMillis();
        redisCacheClient.blacklistToken(token, ttl);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
