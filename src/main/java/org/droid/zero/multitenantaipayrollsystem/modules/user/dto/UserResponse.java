package org.droid.zero.multitenantaipayrollsystem.modules.user.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email
) {}
