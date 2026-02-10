package org.droid.zero.multitenantaipayrollsystem.modules.allowance.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import javax.validation.constraints.DecimalMin;

public record AllowanceRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "Allowance type is required")
        String allowanceType,

        @NotNull(message = "Allowance amount is required")
        @DecimalMax(value = "999999.99", message = "Allowance amount must be less than or equal to 999999.99")
        @DecimalMin(value = "0.01", message = "Allowance amount must be greater than 0")
        Double amount,
        Boolean taxable
){}