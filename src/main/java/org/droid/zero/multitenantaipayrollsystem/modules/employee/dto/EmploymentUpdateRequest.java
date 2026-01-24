package org.droid.zero.multitenantaipayrollsystem.modules.employee.dto;

import jakarta.validation.constraints.NotBlank;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYMENT_TYPE;

import java.util.Date;

public record EmploymentUpdateRequest(
        @NotBlank(message = "joining date is required")
        Date joiningDate,
        EMPLOYMENT_TYPE employmentType
) {}
