package org.droid.zero.multitenantaipayrollsystem.modules.salary.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceDatesUpdateRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.service.SalaryAllowanceService;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("${api.endpoint.base-url}/salary-allowances")
@RequiredArgsConstructor
public class SalaryAllowanceController {
    private final SalaryAllowanceService salaryAllowanceService;

    @GetMapping("/{salaryAllowanceId}")
    @ResponseStatus(OK)
    public ResponseFactory<SalaryAllowanceResponse> findSalaryAllowance(
            @PathVariable UUID salaryAllowanceId
    ) {
        return ResponseFactory.success(
                "Find One Success",
                salaryAllowanceService.findById(salaryAllowanceId));
    }

    @PostMapping()
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<SalaryAllowanceResponse> createSalaryAllowance(
            @Valid @RequestBody SalaryAllowanceRequest salaryAllowanceRequest
    ) {
        return ResponseFactory.created(
                "Create Success",
                salaryAllowanceService.save(salaryAllowanceRequest)
        );
    }

    @PutMapping("/{salaryAllowanceId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<SalaryAllowanceResponse> updateSalaryAllowanceDates(
            @PathVariable UUID salaryAllowanceId,
            @Valid @RequestBody SalaryAllowanceDatesUpdateRequest updateRequest
    ) {
        return ResponseFactory.success(
                "Update Success",
                salaryAllowanceService.updateEffectiveDates(salaryAllowanceId, updateRequest)
        );
    }


}