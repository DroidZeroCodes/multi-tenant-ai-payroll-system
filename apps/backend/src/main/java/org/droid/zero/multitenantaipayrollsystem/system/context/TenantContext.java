package org.droid.zero.multitenantaipayrollsystem.system.context;

import java.util.UUID;

public class TenantContext {

    private static final ThreadLocal<UUID> currentTenant = new ThreadLocal<>();
    private static final UUID rootTenant = UUID.randomUUID();

    public static void setTenantId(UUID tenantId) {
        currentTenant.set(tenantId);
    }

    public static UUID getTenantId() {
        return currentTenant.get();
    }

    public static UUID getRootTenantId() {
        return rootTenant;
    }

    // Critical: Always clear to prevent memory leaks in thread pools
    public static void clear() {
        currentTenant.remove();
    }
}
