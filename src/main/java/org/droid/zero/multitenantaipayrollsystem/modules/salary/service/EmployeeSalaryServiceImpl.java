package org.droid.zero.multitenantaipayrollsystem.modules.salary.service;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.model.Employee;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.repository.EmployeeRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.EmployeeSalaryChangeRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.EmployeeSalaryRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.EmployeeSalaryResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.mapper.EmployeeSalaryMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.EmployeeSalary;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.SalaryStructure;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.repository.EmployeeSalaryRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.repository.SalaryStructureRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.repository.specs.EmployeeSalarySpecs;
import org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole;
import org.droid.zero.multitenantaipayrollsystem.system.BaseService;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.context.UserContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.DuplicateResourceException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ResourceNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ValidationException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.*;

/**
 * Service for managing employee salaries.
 */
@Service
@RequiredArgsConstructor
public class EmployeeSalaryServiceImpl extends BaseService implements EmployeeSalaryService {

    private final EmployeeSalaryRepository employeeSalaryRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeSalaryMapper employeeSalaryMapper;

    /**
     * Finds an employee salary by its ID.
     *
     * @param employeeSalaryId the ID of the employee salary
     * @return the employee salary
     * @throws ResourceNotFoundException if the employee salary is not found
     */
    @Transactional
    @Override
    public EmployeeSalaryResponse findById(UUID employeeSalaryId) {
        EmployeeSalary requestedEmployeeSalary = this.employeeSalaryRepository.findById(employeeSalaryId)
                .orElseThrow(()-> new ResourceNotFoundException(EMPLOYEE_SALARY, employeeSalaryId));

        // Check if the user has access to the employee salary
        if (UserContext.isNot(UserRole.SUPER_ADMIN, UserRole.TENANT_ADMIN, UserRole.PAYROLL_OFFICER)) {
            if (!requestedEmployeeSalary
                    .getEmployee()
                    .getId()
                    .equals(UserContext.getEmployeeId())) {
                throw new ResourceNotFoundException(EMPLOYEE_SALARY, employeeSalaryId);
            }
        }

        return this.employeeSalaryMapper.toResponse(requestedEmployeeSalary);
    }

    /**
     * Finds an employee salary by their employee ID and effective date.
     *
     * @param employeeId the ID of the employee
     * @param date       the effective date
     * @return the employee salary
     * @throws ResourceNotFoundException if the employee salary is not found
     */
    @Transactional
    @Override
    public EmployeeSalaryResponse findByEmployeeIdAndDate(UUID employeeId, LocalDate date) {
        // Check if the user has access to the employee
        if (UserContext.isNot(UserRole.SUPER_ADMIN, UserRole.TENANT_ADMIN, UserRole.PAYROLL_OFFICER)) {
            if (!employeeId.equals(UserContext.getEmployeeId())) {
                throw new ResourceNotFoundException(EMPLOYEE, employeeId);
            }
        }

        EmployeeSalary requestedEmployeeSalary = this.employeeSalaryRepository.findFirst(
                Specification.allOf(
                        EmployeeSalarySpecs.hasEmployeeId(employeeId),
                        EmployeeSalarySpecs.effectiveOn(date)
                    )
                )
                .orElseThrow(()-> new ResourceNotFoundException(EMPLOYEE_SALARY, employeeId));

        return this.employeeSalaryMapper.toResponse(requestedEmployeeSalary);
    }

    /**
     * Saves an employee salary.
     *
     * @param request the employee salary request
     * @return the saved employee salary
     * @throws ResourceNotFoundException  if the salary structure is not found
     * @throws ValidationException        if the salary structure is not active
     * @throws DuplicateResourceException if there is a duplicate field
     */
    @Transactional
    @Override
    public EmployeeSalaryResponse save(EmployeeSalaryRequest request) {
        SalaryStructure salaryStructure = this.salaryStructureRepository.findById(request.salaryStructureId())
                .orElseThrow(() -> new ResourceNotFoundException(SALARY_STRUCTURE, request.salaryStructureId()));

        // Check if the salary structure is active
        if (!salaryStructure.isActive()){
            throw new ValidationException("Salary structure is not active");
        }

        Employee employee = this.employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException(EMPLOYEE, request.employeeId()));

        // Check for duplicate fields
        new FieldDuplicateValidator()
                .addField(employeeSalaryRepository.existsBySalaryStructureIdAndEmployeeId(
                        request.salaryStructureId(), request.employeeId()
                ), "salaryStructureId and employeeId")
                .validate(EMPLOYEE_SALARY);

        EmployeeSalary newEmployeeSalary = new EmployeeSalary(
                salaryStructure,
                employee,
                request.effectiveDate(),
                TenantContext.getTenantId()
        );

        EmployeeSalary savedEmployeeSalary = this.employeeSalaryRepository.save(newEmployeeSalary);
        return this.employeeSalaryMapper.toResponse(savedEmployeeSalary);
    }

    /**
     * Updates an employee salary.
     *
     * @param employeeSalaryId the ID of the employee salary
     * @param request          the employee salary change request
     * @return the updated employee salary
     * @throws ResourceNotFoundException if the employee salary or the salary structure is not found
     * @throws ValidationException       if the salary structure is not active
     */
    @Transactional
    @Override
    public EmployeeSalaryResponse changeSalary(UUID employeeSalaryId, EmployeeSalaryChangeRequest request) {
        EmployeeSalary employeeSalary = this.employeeSalaryRepository.findById(employeeSalaryId)
                .orElseThrow(()-> new ResourceNotFoundException(EMPLOYEE_SALARY, employeeSalaryId));

        SalaryStructure salaryStructure = this.salaryStructureRepository.findById(request.salaryStructureId())
                .orElseThrow(() -> new ResourceNotFoundException(SALARY_STRUCTURE, request.salaryStructureId()));

        // Check if the salary structure is active
        if (!salaryStructure.isActive()){
            throw new ValidationException("Salary structure is not active");
        }

        EmployeeSalary newEmployeeSalary = employeeSalary.changeSalary(
                salaryStructure,
                request.effectiveDate(),
                request.oldSalaryEndDate()
        );

        return this.employeeSalaryMapper.toResponse(this.employeeSalaryRepository.save(newEmployeeSalary));
    }

    /**
     * Updates the effective date of an employee salary.
     *
     * @param employeeSalaryId the ID of the employee salary
     * @param effectiveDate    the new effective date
     * @return the updated employee salary
     * @throws ResourceNotFoundException if the employee salary is not found
     * @throws ValidationException       if the new effective date is before the old effective date
     */
    @Transactional
    @Override
    public EmployeeSalaryResponse updateEffectiveDate(UUID employeeSalaryId, LocalDate effectiveDate) {
        EmployeeSalary employeeSalary = this.employeeSalaryRepository.findById(employeeSalaryId)
                .orElseThrow(()-> new ResourceNotFoundException(EMPLOYEE_SALARY, employeeSalaryId));

        // Check if the new effective date is before the old effective date
        if (effectiveDate.isBefore(employeeSalary.getEffectiveDate())) {
            throw new ValidationException("New effective date cannot be before the old effective date");
        }

        employeeSalary.updateSalaryEffectiveDate(effectiveDate);

        return this.employeeSalaryMapper.toResponse(employeeSalary);
    }

    /**
     * Updates the end date of an employee salary.
     *
     * @param employeeSalaryId the ID of the employee salary
     * @param endDate          the new end date
     * @return the updated employee salary
     * @throws ResourceNotFoundException if the employee salary is not found
     */
    @Transactional
    @Override
    public EmployeeSalaryResponse updateEndDate(UUID employeeSalaryId, LocalDate endDate) {
        EmployeeSalary employeeSalary = this.employeeSalaryRepository.findById(employeeSalaryId)
                .orElseThrow(()-> new ResourceNotFoundException(EMPLOYEE_SALARY, employeeSalaryId));

        employeeSalary.updateSalaryEndDate(endDate);

        return this.employeeSalaryMapper.toResponse(employeeSalary);
    }

}
