package org.droid.zero.multitenantaipayrollsystem.modules.salary.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryStructureRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryStructureResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.service.SalaryStructureService;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("${api.endpoint.base-url}/salaries")
@RequiredArgsConstructor
public class SalaryController {
    private final SalaryStructureService salaryStructureService;

    @GetMapping("/{salaryId}")
    @ResponseStatus(OK)
    public ResponseFactory<SalaryStructureResponse> findSalary(
            @PathVariable UUID salaryId
    ) {
        return ResponseFactory.success(
                "Find One Success",
                salaryStructureService.findById(salaryId));
    }

    @PostMapping()
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<SalaryStructureResponse> createSalary(
            @Valid @RequestBody SalaryStructureRequest salaryStructureRequest
    ) {
        return ResponseFactory.created(
                "Create Success",
                salaryStructureService.save(salaryStructureRequest)
        );
    }

    @PutMapping("/{salaryId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<SalaryStructureResponse> updateSalary(
            @PathVariable UUID salaryId,
            @Valid @RequestBody SalaryStructureRequest salaryStructureRequest
    ) {
        return ResponseFactory.success(
                "Update Success",
                salaryStructureService.update(salaryStructureRequest, salaryId)
        );
    }

    @PatchMapping("/{salaryId}/status")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<?> updateSalaryStatus(@PathVariable UUID salaryId) {
        salaryStructureService.toggleSalaryStatus(salaryId);
        return ResponseFactory.success(
                "Update Success",
                null
        );
    }

}