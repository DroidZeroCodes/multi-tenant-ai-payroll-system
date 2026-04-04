package org.droid.zero.multitenantaipayrollsystem.modules.tenant.events;

import java.util.UUID;

public record TenantCreatedEvent(
        UUID tenantId,
        String name,
        String email,
        String generatedPassword
) {
}
