package org.droid.zero.multitenantaipayrollsystem.modules.department.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(

        @NotBlank(message = "name is required")
        String name,

        @NotBlank(message = "description is required")
        String description
){}
