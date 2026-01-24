package org.droid.zero.multitenantaipayrollsystem.modules.salary.dto;

import jakarta.validation.constraints.NotNull;

import java.sql.Date;

public record SalaryAllowanceDatesUpdateRequest(
        @NotNull(message = "effective date is required")
        Date effectiveDate,
        @NotNull(message = "expiry date is required")
        Date expiryDate
) {
}
