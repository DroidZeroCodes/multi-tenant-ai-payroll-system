package org.droid.zero.multitenantaipayrollsystem.modules.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYEE_STATUS;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYMENT_TYPE;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeRequest(
        @NotNull(message = "User ID is required")
        UUID userId,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotBlank(message = "Phone number is required")
        String phone,

        @NotNull(message = "Date of birth is required")
        LocalDate dob,

        @NotBlank(message = "Address is required")
        String address,

        @NotBlank(message = "Gender is required")
        String gender,

        @NotNull(message = "Joining date is required")
        LocalDate joiningDate,

        EMPLOYMENT_TYPE employmentType,

        EMPLOYEE_STATUS status,

        UUID departmentId,

        UUID positionId
){}
