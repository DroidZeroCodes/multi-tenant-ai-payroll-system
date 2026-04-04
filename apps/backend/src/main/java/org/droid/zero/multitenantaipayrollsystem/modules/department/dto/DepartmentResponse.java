package org.droid.zero.multitenantaipayrollsystem.modules.department.dto;

import java.util.UUID;

public record DepartmentResponse(
        UUID id,
        String name,
        String description,
        boolean active
){}
