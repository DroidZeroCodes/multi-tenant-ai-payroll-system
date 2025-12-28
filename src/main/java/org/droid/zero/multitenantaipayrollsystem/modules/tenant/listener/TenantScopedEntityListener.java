package org.droid.zero.multitenantaipayrollsystem.modules.tenant.listener;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.TenantScopedEntity;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.context.UserContext;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole.SUPER_ADMIN;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantScopedEntityListener {
    private static final ThreadLocal<Boolean> SKIP_TENANT_CHECK = ThreadLocal.withInitial(() -> false);

    public static void runWithoutTenantChecks(Runnable action) {
        SKIP_TENANT_CHECK.set(true);
        try {
            action.run();
        } finally {
            SKIP_TENANT_CHECK.remove();
        }
    }

    @PrePersist
    @PreUpdate
    @PreRemove
    @PostLoad
    public void checkTenant(Object entity) {
        if (SKIP_TENANT_CHECK.get()) return;
        if (!(entity instanceof TenantScopedEntity)) return;
        if (UserContext.hasRole(SUPER_ADMIN)) return;

        UUID currentTenantIdentifier = TenantContext.getTenantId();
        UUID entityTenantIdentifier = ((TenantScopedEntity) entity).getTenantId();
        if (!Objects.equals(currentTenantIdentifier, entityTenantIdentifier)) {
            log.warn("Entity's tenantId does not match current tenant: currentTenantIdentifier={}, entity={}", currentTenantIdentifier, entity);
            throw new IllegalArgumentException("Entity's tenantId does not match current tenant");
        }
    }
}