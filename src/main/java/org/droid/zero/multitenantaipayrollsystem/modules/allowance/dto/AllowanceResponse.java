package org.droid.zero.multitenantaipayrollsystem.modules.allowance.dto;

import java.util.UUID;

public record AllowanceResponse(
        UUID id,
        String name,
        String description,
        String allowanceType,
        Double amount,
        boolean taxable
){}
