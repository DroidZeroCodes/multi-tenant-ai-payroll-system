package org.droid.zero.multitenantaipayrollsystem.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "oldPassword is required")
        String oldPassword,

        @NotBlank(message = "newPassword is required")
        String newPassword,

        @NotBlank(message = "confirmPassword is required")
        String confirmPassword
) {
}
