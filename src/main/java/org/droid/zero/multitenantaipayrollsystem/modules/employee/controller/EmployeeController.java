package org.droid.zero.multitenantaipayrollsystem.modules.employee.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeResponse;
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

}