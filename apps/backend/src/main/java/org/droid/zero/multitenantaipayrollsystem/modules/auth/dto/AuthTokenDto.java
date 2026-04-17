package org.droid.zero.multitenantaipayrollsystem.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole;

import java.util.UUID;

public record AuthTokenDto(
        @org.hibernate.validator.constraints.UUID
        @NotNull
        UUID id,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        String username,
        @NotBlank
        String token
) {}
