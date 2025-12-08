package org.droid.zero.multitenantaipayrollsystem.tenant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TenantRequest(
        @NotBlank(message = "name is required")
        String name,

        @NotBlank(message = "email is required")
        @Email(message = "invalid email format")
        String email,

        @NotBlank(message = "phone is required")

        String phone,

        @NotBlank(message = "industry is required")
        String industry
) {
}
