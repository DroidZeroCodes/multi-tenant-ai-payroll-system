package org.droid.zero.multitenantaipayrollsystem.tenant.dto;

import java.util.UUID;

public record TenantStatus(
        UUID id,
        boolean active
) {}
