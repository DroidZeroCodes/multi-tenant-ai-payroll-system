package org.droid.zero.multitenantaipayrollsystem.modules.employee.service;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.department.repository.DepartmentRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYEE_STATUS;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeInfoUpdateRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmploymentUpdateRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.mapper.EmployeeMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.model.Employee;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.util.EmployeeCodeGenerator;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.repository.EmployeeRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.position.repository.PositionRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.repository.UserRepository;
import org.droid.zero.multitenantaipayrollsystem.system.BaseService;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ResourceNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.*;


@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl extends BaseService implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional
    @Override
    public EmployeeResponse findById(UUID employeeId) {
        //Fetch the requested employee (or fail with 404)
        Employee requestedEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE, employeeId));

        return employeeMapper.toResponse(requestedEmployee);
    }

    @Transactional
    @Override
    public EmployeeResponse save(EmployeeRequest request) {
        if (!userRepository.existsById(request.userId())) throw new ResourceNotFoundException(USER, request.userId());
        if (request.positionId() != null && !positionRepository.existsById(request.positionId())) throw new ResourceNotFoundException(POSITION, request.positionId());
        if (request.departmentId() != null && !departmentRepository.existsById(request.departmentId())) throw new ResourceNotFoundException(DEPARTMENT, request.departmentId());

        String employeeCode = EmployeeCodeGenerator.generate(request.joiningDate(), employeeRepository);

        //Validate if the provided arguments does not violate unique constraints
        new FieldDuplicateValidator()
                .addField(employeeRepository.existsByEmployeeCode(employeeCode), "employeeCode")
                .validate(EMPLOYEE);

        //Convert request to an entity to be persisted and set the tenant
        Employee employee = new Employee(
                request.userId(),
                employeeCode,
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phone(),
                request.address(),
                request.gender(),
                request.dob(),
                request.joiningDate(),
                request.employmentType(),
                request.status(),
                request.departmentId(),
                request.positionId(),
                TenantContext.getTenantId()
        );

        //Create the new employee record in the database
        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Transactional
    @Override
    public EmployeeResponse updateInfo(EmployeeInfoUpdateRequest updatedEmployee, UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE, employeeId));

        employee.updateEmployeeInfo(
                updatedEmployee.firstName(),
                updatedEmployee.lastName(),
                updatedEmployee.email(),
                updatedEmployee.phone(),
                updatedEmployee.dob(),
                updatedEmployee.address(),
                updatedEmployee.gender()
        );
        return employeeMapper.toResponse(employee);
    }

    @Transactional
    @Override
    public EmployeeResponse updateEmployment(EmploymentUpdateRequest updatedEmployee, UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE, employeeId));

        employee.updateEmploymentDetails(
                updatedEmployee.joiningDate(),
                updatedEmployee.employmentType()
        );
        return employeeMapper.toResponse(employee);
    }

    @Transactional
    @Override
    public EmployeeResponse updateEmployeeStatus(UUID employeeId, EMPLOYEE_STATUS status) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE, employeeId));

        employee.changeEmployeeStatus(status);
        return employeeMapper.toResponse(employee);
    }

    @Transactional
    @Override
    public EmployeeResponse updateEmployeePosition(UUID employeeId, UUID positionId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE, employeeId));

        if (positionId != null && !positionRepository.existsById(positionId)) throw new ResourceNotFoundException(POSITION, positionId);

        employee.assignPosition(positionId);
        return employeeMapper.toResponse(employee);
    }

    @Transactional
    @Override
    public EmployeeResponse updateEmployeeDepartment(UUID employeeId, UUID departmentId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE, employeeId));

        if (departmentId != null && !departmentRepository.existsById(departmentId)) throw new ResourceNotFoundException(DEPARTMENT, departmentId);

        employee.assignDepartment(departmentId);
        return employeeMapper.toResponse(employee);
    }

}


