package org.droid.zero.multitenantaipayrollsystem.system.util;

import jakarta.servlet.http.HttpServletRequest;

public class HeaderUtils {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String AUTHENTICATION_HEADER ="Authorization";

    public static String extractJwt(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHENTICATION_HEADER);
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim().replaceAll("\\s+", "");
        }
        return token;
    }

    public static String extractTenantId(HttpServletRequest request) {
        return request.getHeader(TENANT_HEADER);
    }

}
