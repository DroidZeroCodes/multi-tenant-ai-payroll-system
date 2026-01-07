package org.droid.zero.multitenantaipayrollsystem.modules.position.dto;

import jakarta.validation.constraints.NotBlank;

public record PositionRequest (
        @NotBlank(message = "title is required")
        String title,
        @NotBlank(message = "description is required")
        String description,
        @NotBlank(message = "level is required")
        String level
){}