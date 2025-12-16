package org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto;

import java.util.UUID;

public record TenantStatus(
        UUID id,
        boolean active
) {}
