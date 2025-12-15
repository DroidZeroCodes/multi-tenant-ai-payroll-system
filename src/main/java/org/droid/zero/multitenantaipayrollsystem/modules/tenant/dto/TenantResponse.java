package org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto;

import java.util.UUID;

public record TenantResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String industry,
        Boolean active
) {}
