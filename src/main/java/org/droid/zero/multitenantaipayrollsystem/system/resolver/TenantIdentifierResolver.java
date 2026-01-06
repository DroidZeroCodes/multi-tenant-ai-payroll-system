package org.droid.zero.multitenantaipayrollsystem.system.resolver;

import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.nullness.qual.UnknownKeyFor;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext.getRootTenantId;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<UUID>, HibernatePropertiesCustomizer {

    @Override
    public UUID resolveCurrentTenantIdentifier() {
        return Objects.requireNonNullElse(TenantContext.getTenantId(), UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @Override
    public @UnknownKeyFor @Initialized boolean validateExistingCurrentSessions() {
        return true;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }

    @Override
    public @UnknownKeyFor @Initialized boolean isRoot(UUID tenantId) {
        return Objects.equals(tenantId, getRootTenantId());
    }
}
