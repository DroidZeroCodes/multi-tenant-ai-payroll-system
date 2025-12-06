package org.droid.zero.multitenantaipayrollsystem.tenant.dto;

import java.util.UUID;

public record TenantResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String industry,
        boolean active
) {}
