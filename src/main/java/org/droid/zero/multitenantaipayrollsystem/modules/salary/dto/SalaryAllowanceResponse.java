package org.droid.zero.multitenantaipayrollsystem.modules.salary.dto;

import java.time.LocalDate;
import java.util.UUID;

public record SalaryAllowanceResponse(
        UUID id,
        UUID salaryStructureId,
        UUID allowanceId,
        LocalDate effectiveDate,
        LocalDate expiryDate,
        String assignedBy
){}
