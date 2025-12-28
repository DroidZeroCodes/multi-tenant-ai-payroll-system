package org.droid.zero.multitenantaipayrollsystem.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.security.jwt.TokenService;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.util.HeaderUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TenantContextFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    @Value("${api.endpoint.base-url}")
    private String baseUrl;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        boolean isAuthLoginEndpoint = request.getRequestURI().equals(baseUrl + "/auth/login");
        String tenantId = "";

        // 1. Try to extract tenantId from the header if from the login endpoint, else extract it from the JWT
        if (isAuthLoginEndpoint) {
             tenantId = HeaderUtils.extractTenantId(request);
        } else {
            String jwt = HeaderUtils.extractJwt(request);

            if (jwt != null && !jwt.isBlank()) {
                tenantId = tokenService.extractClaim(jwt, claims -> claims.get("tenantId", String.class));
            }
        }

        try {
            if (tenantId != null && !tenantId.isBlank()) {
                try {
                    TenantContext.setTenantId(UUID.fromString(tenantId));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid tenant Id value: " + tenantId);
                }
            }

            // 2. Continue the chain (This goes to Spring Security next)
            filterChain.doFilter(request, response);

        } finally {
            // 3. Always clear context after the request finishes
            TenantContext.clear();
        }
    }
}
