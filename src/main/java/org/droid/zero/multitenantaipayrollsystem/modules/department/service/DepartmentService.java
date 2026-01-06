package org.droid.zero.multitenantaipayrollsystem.modules.department.service;

import org.droid.zero.multitenantaipayrollsystem.modules.department.dto.DepartmentRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.department.dto.DepartmentResponse;

import java.util.UUID;

public interface DepartmentService  {

    DepartmentResponse findById(UUID departmentId);

    DepartmentResponse save(DepartmentRequest request);

    DepartmentResponse update(DepartmentRequest request, UUID departmentId);

    void toggleDepartmentStatus(UUID departmentId);
}
