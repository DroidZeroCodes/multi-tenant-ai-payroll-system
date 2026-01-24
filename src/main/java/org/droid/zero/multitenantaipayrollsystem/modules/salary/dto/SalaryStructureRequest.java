package org.droid.zero.multitenantaipayrollsystem.modules.salary.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import javax.validation.constraints.DecimalMin;
import java.sql.Date;

public record SalaryStructureRequest(
        @NotBlank(message = "name is required")
        String name,

        @NotBlank(message = "description is required")
        String description,

        @NotNull(message = "base salary is required")
        @DecimalMax(value = "999999.99", message = "base salary must be less than or equal to 999999.99")
        @DecimalMin(value = "0.01", message = "base salary must be greater than 0")
        Double baseSalary,

        @NotNull(message = "effectiveDate is required")
        Date effectiveDate
){}