package org.droid.zero.multitenantaipayrollsystem.modules.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.CredentialsRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole;

import java.util.Set;

public record UserRegistrationRequest(
        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName,

        @NotBlank(message = "contactEmail is required")
        String contactEmail,

        @NotEmpty(message = "roles is required")
        Set<UserRole> roles,

        @NotNull(message = "credentials is required")
        @Valid
        CredentialsRegistrationRequest credentials
) {
}
