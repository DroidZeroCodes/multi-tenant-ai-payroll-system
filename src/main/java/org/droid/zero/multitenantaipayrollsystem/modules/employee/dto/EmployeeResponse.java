package org.droid.zero.multitenantaipayrollsystem.modules.employee.dto;

import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYEE_STATUS;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYMENT_TYPE;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        UUID positionId,
        UUID departmentId,
        String employeeCode,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address,
        String gender,
        LocalDate dob,
        LocalDate joiningDate,
        EMPLOYMENT_TYPE employmentType,
        EMPLOYEE_STATUS status
) {}
