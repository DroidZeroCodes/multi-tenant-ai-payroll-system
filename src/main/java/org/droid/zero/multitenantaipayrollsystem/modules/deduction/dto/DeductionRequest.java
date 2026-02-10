package org.droid.zero.multitenantaipayrollsystem.modules.deduction.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import javax.validation.constraints.DecimalMin;

public record DeductionRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "Deduction type is required")
        String deductionType,

        @NotNull(message = "Deduction amount is required")
        @DecimalMax(value = "999999.99", message = "Deduction amount must be less than or equal to 999999.99")
        @DecimalMin(value = "0.01", message = "Deduction amount must be greater than 0")
        Double amount,
        Boolean taxable
){}