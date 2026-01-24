package org.droid.zero.multitenantaipayrollsystem.modules.allowance.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.dto.AllowanceRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.dto.AllowanceResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.service.AllowanceService;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("${api.endpoint.base-url}/allowances")
@RequiredArgsConstructor
public class AllowanceController {
    private final AllowanceService allowanceService;

    @GetMapping("/{allowanceId}")
    @ResponseStatus(OK)
    public ResponseFactory<AllowanceResponse> findAllowance(
            @PathVariable UUID allowanceId
    ) {
        return ResponseFactory.success(
                "Find One Success",
                allowanceService.findById(allowanceId));
    }

    @PostMapping()
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<AllowanceResponse> createAllowance(
            @Valid @RequestBody AllowanceRequest allowanceRequest
    ) {
        return ResponseFactory.created(
                "Create Success",
                allowanceService.save(allowanceRequest)
        );
    }

    @PutMapping("/{allowanceId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<AllowanceResponse> updateAllowance(
            @PathVariable UUID allowanceId,
            @Valid @RequestBody AllowanceRequest allowanceRequest
    ) {
        return ResponseFactory.success(
                "Update Success",
                allowanceService.update(allowanceRequest, allowanceId)
        );
    }

}