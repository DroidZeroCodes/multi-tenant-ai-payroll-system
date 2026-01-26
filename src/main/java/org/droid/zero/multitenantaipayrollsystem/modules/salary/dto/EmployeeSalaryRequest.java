package org.droid.zero.multitenantaipayrollsystem.modules.salary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeSalaryRequest(
        @NotNull(message = "salary structure ID is required")
        UUID salaryStructureId,
        @NotNull(message = "employee ID is required")
        UUID employeeId,
        @NotBlank(message = "effective date is required")
        LocalDate effectiveDate
){}