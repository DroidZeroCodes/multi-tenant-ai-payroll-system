package org.droid.zero.multitenantaipayrollsystem.modules.department.service;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.department.dto.DepartmentRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.department.dto.DepartmentResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.department.mapper.DepartmentMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.department.model.Department;
import org.droid.zero.multitenantaipayrollsystem.modules.department.repository.DepartmentRepository;
import org.droid.zero.multitenantaipayrollsystem.system.BaseService;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.DEPARTMENT;


@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl extends BaseService implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Transactional
    @Override
    public DepartmentResponse findById(UUID departmentId) {
        Department requestedDepartment = this.departmentRepository.findById(departmentId)
                .orElseThrow(()-> new ObjectNotFoundException(DEPARTMENT, departmentId));

        return this.departmentMapper.toResponse(requestedDepartment);
    }

    @Transactional
    @Override
    public DepartmentResponse save(DepartmentRequest request) {
        new FieldDuplicateValidator()
                .addField(departmentRepository.existsByNameIgnoreCase(request.name()), "name")
                .validate(DEPARTMENT);

        Department newDepartment = new Department(
                request.name(),
                request.description(),
                TenantContext.getTenantId()
        );

        Department savedDepartment = this.departmentRepository.save(newDepartment);
        return this.departmentMapper.toResponse(savedDepartment);
    }

    @Transactional
    @Override
    public DepartmentResponse update(DepartmentRequest request, UUID departmentId) {
        //Find the Department from the repository
        Department existingDepartment = this.departmentRepository.findById(departmentId)
                .orElseThrow(()-> new ObjectNotFoundException(DEPARTMENT, departmentId));

        //Update the department
        existingDepartment.updateDepartmentDetails(
                request.name(),
                request.description()
        );

        return this.departmentMapper.toResponse(existingDepartment);
    }

    @Transactional
    @Override
    public void toggleDepartmentStatus(UUID departmentId) {
        Department existingDepartment = this.departmentRepository.findById(departmentId)
                .orElseThrow(()-> new ObjectNotFoundException(DEPARTMENT, departmentId));

        existingDepartment.toggleActiveStatus();
    }
}
