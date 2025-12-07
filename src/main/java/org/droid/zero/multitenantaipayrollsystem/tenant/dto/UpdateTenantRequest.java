package org.droid.zero.multitenantaipayrollsystem.tenant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTenantRequest(
        @NotBlank(message = "tenant name is required")
        String name,

        @NotBlank(message = "tenant email is required")
        @Email(message = "invalid email format")
        String email,

        @NotBlank(message = "tenant phone is required")
        String phone,

        @NotBlank(message = "tenant industry is required")
        String industry,

        @NotNull(message = "tenant active state is required")
        Boolean active
) {
}
