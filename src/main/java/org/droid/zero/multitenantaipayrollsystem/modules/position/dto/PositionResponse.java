package org.droid.zero.multitenantaipayrollsystem.modules.position.dto;

import java.util.UUID;

public record PositionResponse (
        UUID id,
        String title,
        String description,
        String level,
        boolean active
){}
