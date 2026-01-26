package org.droid.zero.multitenantaipayrollsystem.modules.salary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeSalaryChangeRequest(
        @NotNull(message = "salary structure ID is required")
        UUID salaryStructureId,
        @NotBlank(message = "effective date is required")
        LocalDate effectiveDate,
        @NotBlank(message = "old salary end date is required")
        LocalDate oldSalaryEndDate
){}