package org.droid.zero.multitenantaipayrollsystem.security.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailRequest(
        @NotBlank(message = "email is required")
        @Email(message = "invalid email format")
        String email
) {
}
