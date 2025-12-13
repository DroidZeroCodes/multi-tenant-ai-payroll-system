package org.droid.zero.multitenantaipayrollsystem.tenant.events;

import java.util.UUID;

public record TenantCreatedEvent(
        UUID tenantId,
        String name,
        String email,
        String generatedPassword
) {
}
