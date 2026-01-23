package org.droid.zero.multitenantaipayrollsystem.modules.employee.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.constant.EMPLOYEE_STATUS;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeInfoUpdateRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmploymentUpdateRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.service.EmployeeService;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${api.endpoint.base-url}/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'HR_OFFICER', 'PAYROLL_OFFICER') or #employeeId == @userContext.employeeId")
    public ResponseFactory<EmployeeResponse> findEmployeeById(@PathVariable UUID employeeId) {
        return ResponseFactory.success(
                "Find One Success",
                employeeService.findById(employeeId)
        );
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'HR_OFFICER')")
    public ResponseFactory<EmployeeResponse> addEmployee(@Valid @RequestBody EmployeeRequest newEmployee) {
        EmployeeResponse savedEmployee = this.employeeService.save(newEmployee);
        return ResponseFactory.created(
                "Create Success",
                savedEmployee
        );
    }


    @PatchMapping("/{employeeId}/info")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'HR_OFFICER') or #employeeId == @userContext.employeeId")
    public ResponseFactory<EmployeeResponse> updateEmployeeInfo(@PathVariable UUID employeeId, @Valid @RequestBody EmployeeInfoUpdateRequest updatedEmployee) {
        return ResponseFactory.success(
                "Update Success",
                employeeService.updateInfo(updatedEmployee, employeeId)
        );
    }

    @PatchMapping("/{employeeId}/employment")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'HR_OFFICER') or #employeeId == @userContext.employeeId")
    public ResponseFactory<EmployeeResponse> updateEmployeeEmployment(@PathVariable UUID employeeId, @Valid @RequestBody EmploymentUpdateRequest updatedEmployee) {
        return ResponseFactory.success(
                "Update Success",
                employeeService.updateEmployment(updatedEmployee, employeeId)
        );
    }

    @PatchMapping("/{employeeId}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'HR_OFFICER') or #employeeId == @userContext.employeeId")
    public ResponseFactory<EmployeeResponse> updateEmployeeStatus(@PathVariable UUID employeeId, @RequestBody EMPLOYEE_STATUS status) {
        return ResponseFactory.success(
                "Update Success",
                employeeService.updateEmployeeStatus(employeeId, status)
        );
    }

    @PatchMapping("/{employeeId}/position")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'HR_OFFICER') or #employeeId == @userContext.employeeId")
    public ResponseFactory<EmployeeResponse> updateEmployeePosition(@PathVariable UUID employeeId, @RequestBody UUID positionId) {
        return ResponseFactory.success(
                "Update Success",
                employeeService.updateEmployeePosition(employeeId, positionId)
        );
    }

    @PatchMapping("/{employeeId}/department")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'HR_OFFICER') or #employeeId == @userContext.employeeId")
    public ResponseFactory<EmployeeResponse> updateEmployeeDepartment(@PathVariable UUID employeeId, @RequestBody UUID departmentId) {
        return ResponseFactory.success(
                "Update Success",
                employeeService.updateEmployeeDepartment(employeeId, departmentId)
        );
    }

}