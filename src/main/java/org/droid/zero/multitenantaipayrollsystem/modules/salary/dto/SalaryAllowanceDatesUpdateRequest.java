package org.droid.zero.multitenantaipayrollsystem.modules.salary.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SalaryAllowanceDatesUpdateRequest(
        @NotNull(message = "effective date is required")
        LocalDate effectiveDate,
        @NotNull(message = "expiry date is required")
        LocalDate expiryDate
) {
}
