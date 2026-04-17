package org.droid.zero.multitenantaipayrollsystem.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.client.redis.RedisCacheClient;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.model.Employee;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
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
    @Value("${auth.jwt.expiration.minutes}")
    private long JWT_EXPIRATION_MINUTES;

    private final RedisCacheClient redisCacheClient;

    /**
     * Generates a JWT signing key from the provided secret key.
     *
     * @return SecretKey for signing JWTs.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a signed JWT token for the provided authentication and tenant ID.
     *
     * @param authentication User authentication.
     * @param tenantId       Tenant ID associated with the user.
     * @return JWT token.
     */
    public String generateToken(Authentication authentication, String tenantId) {
        String username = authentication.getName();

        // Retrieve the roles associated with the user
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Instant now = Instant.now();
        Employee employee = ((User)(authentication.getPrincipal())).getEmployee();
        String employeeId = employee != null ? employee.getId().toString() : "";

        return Jwts.builder()
                .subject(username) // Subject represents the user's username
                .issuedAt(Date.from(now)) // Issued at represents the time when the token was issued
                .expiration(Date.from(now.plus(JWT_EXPIRATION_MINUTES, ChronoUnit.MINUTES))) // Expiration represents the time when the token expires
                .claim("employeeId", employeeId) // Additional claim for the employee ID
                .claim("tenantId", tenantId) // Additional claim for the tenant ID
                .claim("roles", roles) // Additional claim for the user's roles
                .signWith(getSigningKey()) // Sign the token with the provided secret key
                .compact(); // Return the compact, URL-safe representation of the token
    }

    /**
     * Extracts the username from the provided JWT token.
     *
     * @param token JWT token.
     * @return Username extracted from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Checks if the provided JWT token is valid for the given user details.
     *
     * @param token        JWT token.
     * @param userDetails  User details.
     * @return True if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        // Extract the username from the token
        final String username = extractUsername(token);

        // Check if the username matches the user details and the token is not expired and not blacklisted
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && !redisCacheClient.isTokenBlacklisted(token));
    }

    /**
     * Blacklists the provided JWT token.
     *
     * @param token        JWT token.
     * @param userDetails  User details.
     */
    public void blacklistToken(String token, UserDetails userDetails) {
        // Check if the token is valid before blacklisting it
        if (isTokenValid(token, userDetails)){
            long ttl = extractClaim(token, Claims::getExpiration).getTime() - System.currentTimeMillis();
            redisCacheClient.blacklistToken(token, ttl);
        }
    }

    /**
     * Extracts a claim from the provided JWT token using the provided claims' resolver.
     *
     * @param token        JWT token.
     * @param claimsResolver Function to resolve claims from the token.
     * @param <T>          Type of the claim.
     * @return The resolved claim value.
     */
    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // Extract all claims from the token
        final Claims claims = extractAllClaims(token);

        // Resolve the claim using the provided resolver
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the provided JWT token.
     *
     * @param token JWT token.
     * @return Claims object containing all claims in the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks if the provided JWT token is expired.
     *
     * @param token JWT token.
     * @return True if the token is expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        // Extract the expiration claim from the token
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}

