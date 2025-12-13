package org.droid.zero.multitenantaipayrollsystem.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.droid.zero.multitenantaipayrollsystem.security.auth.dto.CredentialsRegistrationRequest;

import java.util.UUID;

public record UserRegistrationRequest(
        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName,

        @NotNull(message = "credentials is required")
        @Valid
        CredentialsRegistrationRequest credentials,

        @NotNull(message = "tenantId is required")
        UUID tenantId
) {
}
