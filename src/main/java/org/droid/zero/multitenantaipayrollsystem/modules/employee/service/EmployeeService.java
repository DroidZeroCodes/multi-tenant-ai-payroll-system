package org.droid.zero.multitenantaipayrollsystem.modules.employee.service;


import jakarta.validation.Valid;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYEE_STATUS;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeInfoUpdateRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmploymentUpdateRequest;

import java.util.UUID;

public interface EmployeeService {
    EmployeeResponse findById(UUID userId);

    EmployeeResponse save(EmployeeRequest request);

    EmployeeResponse updateInfo(@Valid EmployeeInfoUpdateRequest updatedEmployee, UUID employeeId);

    EmployeeResponse updateEmployment(EmploymentUpdateRequest updatedEmployee, UUID employeeId);

    EmployeeResponse updateEmployeeStatus(UUID employeeId, EMPLOYEE_STATUS status);

    EmployeeResponse updateEmployeePosition(UUID employeeId, UUID positionId);

    EmployeeResponse updateEmployeeDepartment(UUID employeeId, UUID departmentId);

}
