package org.droid.zero.multitenantaipayrollsystem.modules.employee.dto;

import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYEE_STATUS;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYMENT_TYPE;

import java.util.Date;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String employeeCode,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address,
        String gender,
        Date dob,
        Date joiningDate,
        EMPLOYMENT_TYPE employmentType,
        EMPLOYEE_STATUS status,
        String positionTitle,
        String departmentName
) {}
