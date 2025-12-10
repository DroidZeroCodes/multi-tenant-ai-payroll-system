package org.droid.zero.multitenantaipayrollsystem.security.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.droid.zero.multitenantaipayrollsystem.user.UserRole;

import java.util.Set;

public record CredentialsRegistrationRequest(
        @NotBlank(message = "email is required")
        @Email(message = "invalid email format")
        String email,

        @NotBlank(message = "password is required")
        String password,

        @NotBlank(message = "confirmPassword is required")
        String confirmPassword,

        @NotEmpty(message = "role is required")
        Set<UserRole> role
) {
}
