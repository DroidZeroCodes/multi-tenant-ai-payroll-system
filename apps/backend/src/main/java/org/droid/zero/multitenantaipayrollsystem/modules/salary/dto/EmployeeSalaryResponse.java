package org.droid.zero.multitenantaipayrollsystem.modules.salary.dto;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeSalaryResponse(
        UUID id,
        UUID salaryStructureId,
        UUID employeeId,
        LocalDate effectiveDate,
        LocalDate endDate,
        String assignedBy
){}
