package org.droid.zero.multitenantaipayrollsystem.modules.salary.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SalaryAllowanceDatesUpdateRequest(
        @NotNull(message = "Effective date is required")
        LocalDate effectiveDate,
        @NotNull(message = "Expiry date is required")
        LocalDate expiryDate
) {
}
