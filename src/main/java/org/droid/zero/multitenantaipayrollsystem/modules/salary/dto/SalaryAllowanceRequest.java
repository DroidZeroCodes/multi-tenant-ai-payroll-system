package org.droid.zero.multitenantaipayrollsystem.modules.salary.dto;

import jakarta.validation.constraints.NotBlank;

import java.sql.Date;
import java.util.UUID;

public record SalaryAllowanceRequest(
        @NotBlank(message = "salary structure ID is required")
        UUID salaryStructureId,
        @NotBlank(message = "allowance ID is required")
        UUID allowanceId,
        @NotBlank(message = "effective date is required")
        Date effectiveDate,
        @NotBlank(message = "expiry date is required")
        Date expiryDate
){}