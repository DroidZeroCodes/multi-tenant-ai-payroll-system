package org.droid.zero.multitenantaipayrollsystem.modules.deduction.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.deduction.dto.DeductionRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.deduction.dto.DeductionResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.deduction.service.DeductionService;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("${api.endpoint.base-url}/deductions")
@RequiredArgsConstructor
public class DeductionController {
    private final DeductionService deductionService;

    @GetMapping("/{deductionId}")
    @ResponseStatus(OK)
    public ResponseFactory<DeductionResponse> findDeduction(
            @PathVariable UUID deductionId
    ) {
        return ResponseFactory.success(
                "Find One Success",
                deductionService.findById(deductionId));
    }

    @PostMapping()
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<DeductionResponse> createDeduction(
            @Valid @RequestBody DeductionRequest deductionRequest
    ) {
        return ResponseFactory.created(
                "Create Success",
                deductionService.save(deductionRequest)
        );
    }

    @PutMapping("/{deductionId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<DeductionResponse> updateDeduction(
            @PathVariable UUID deductionId,
            @Valid @RequestBody DeductionRequest deductionRequest
    ) {
        return ResponseFactory.success(
                "Update Success",
                deductionService.update(deductionRequest, deductionId)
        );
    }

}