package org.droid.zero.multitenantaipayrollsystem.modules.department.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description
){}
