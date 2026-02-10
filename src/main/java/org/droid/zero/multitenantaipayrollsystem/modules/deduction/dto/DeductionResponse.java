package org.droid.zero.multitenantaipayrollsystem.modules.deduction.dto;

import java.util.UUID;

public record DeductionResponse(
        UUID id,
        String name,
        String description,
        String deductionType,
        Double amount,
        boolean taxable
){}
