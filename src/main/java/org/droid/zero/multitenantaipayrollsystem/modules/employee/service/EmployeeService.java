package org.droid.zero.multitenantaipayrollsystem.modules.employee.service;


import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeResponse;

import java.util.UUID;

public interface EmployeeService {
    EmployeeResponse findById(UUID userId);

    EmployeeResponse save(EmployeeRequest request);

}
