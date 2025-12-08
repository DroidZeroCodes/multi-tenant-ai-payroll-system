package org.droid.zero.multitenantaipayrollsystem.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.droid.zero.multitenantaipayrollsystem.user.UserRole;

import java.util.Set;
import java.util.UUID;

public record UserRequest(
        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName,

        @NotBlank(message = "email is required")
        @Email(message = "invalid email format")
        String email,

        @NotBlank(message = "password is required")
        String password,

        @NotEmpty(message = "role is required")
        Set<UserRole> role,

        @NotNull(message = "tenantId is required")
        UUID tenantId
) {
}
