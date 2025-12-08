package org.droid.zero.multitenantaipayrollsystem.user.dto;

import java.util.UUID;

public record UserStatus(
        UUID id,
        boolean active
) {}
