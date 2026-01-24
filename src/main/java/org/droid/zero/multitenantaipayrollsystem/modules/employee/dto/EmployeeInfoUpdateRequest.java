package org.droid.zero.multitenantaipayrollsystem.modules.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;
import java.util.UUID;

public record EmployeeInfoUpdateRequest(
        @NotBlank(message = "userId is required")
        UUID userId,

        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName,

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        String email,

        @NotBlank(message = "phone is required")
        String phone,

        @NotBlank(message = "date of birth is required")
        Date dob,

        @NotBlank(message = "address is required")
        String address,

        @NotBlank(message = "gender is required")
        String gender
) {
}
