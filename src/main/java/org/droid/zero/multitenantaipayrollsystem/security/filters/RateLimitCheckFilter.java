package org.droid.zero.multitenantaipayrollsystem.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.client.redis.RedisCacheClient;
import org.droid.zero.multitenantaipayrollsystem.system.api.ErrorObject;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Component
@RequiredArgsConstructor
public class RateLimitCheckFilter extends OncePerRequestFilter {

    private final RedisCacheClient redis;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        // 1. Get the email from the request header
        String email = null;

        try {
            email = extractBasicAuthUsername(request);
        } catch (Exception ignored) {}

        var error = ResponseFactory.error(
                "Login attempts exceeded. Please try again later.",
                Collections.singletonList(new ErrorObject(
                        TOO_MANY_REQUESTS,
                        "rate_limit_exceeded",
                        "Too Many Requests",
                        "Login attempts exceeded. Please try again later.",
                        new ErrorObject.Source("rate_limit")
                )));

        if (email != null) {
            // 2. Check the rate limit in Redis
            if (redis.isRateLimitExceeded(email)) {
                response.setStatus(TOO_MANY_REQUESTS.value());
                response.getWriter().write(objectMapper.writeValueAsString(error));
                return;
            }
        }

        // 3. Continue to the standard Spring Security filters (UsernamePasswordAuthenticationFilter)
        filterChain.doFilter(request, response);
    }

    public String extractBasicAuthUsername(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Basic ")) {
                return null; // or throw exception if required
            }

            // Remove "Basic " prefix
            String base64Credentials = authHeader.substring(6);
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String decodedCreds = new String(decodedBytes, StandardCharsets.UTF_8);

            // decodedCreds format is: username:password
            int colonIndex = decodedCreds.indexOf(':');
            if (colonIndex == -1) {
                return null; // malformed
            }

            return decodedCreds.substring(0, colonIndex);
        } catch (Exception e) {
            throw new BadCredentialsException("Malformed authorization request");
        }
    }
}