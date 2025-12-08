package org.droid.zero.multitenantaipayrollsystem.user.dto;

import org.droid.zero.multitenantaipayrollsystem.user.UserRole;

import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Set<UserRole> role,
        boolean active
) {}
