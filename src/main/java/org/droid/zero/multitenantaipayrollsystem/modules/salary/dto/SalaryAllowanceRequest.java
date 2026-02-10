package org.droid.zero.multitenantaipayrollsystem.modules.salary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record SalaryAllowanceRequest(
        @NotNull(message = "Allowance ID is required")
        UUID allowanceId,
        @NotBlank(message = "Effective date is required")
        LocalDate effectiveDate,
        @NotBlank(message = "Expiry date is required")
        LocalDate expiryDate
){}