package org.droid.zero.multitenantaipayrollsystem.modules.salary.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceDatesUpdateRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.service.SalaryAllowanceService;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ResourceNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ValidationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

/**
 * REST Controller for managing salary allowances in the multi-tenant AI payroll system.
 * 
 * <p>This controller handles CRUD operations for salary allowances, which are additional
 * compensation components that can be added to an employee's base salary. Examples include
 * housing allowances, transportation allowances, and other supplemental payments.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Create new salary allowances with validation</li>
 *   <li>Retrieve existing salary allowances by ID</li>
 *   <li>Update effective dates for existing allowances</li>
 *   <li>Role-based access control for sensitive operations</li>
 * </ul>
 * 
 * @author Multi-Tenant AI Payroll System
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("${api.endpoint.base-url}/salary-allowances")
@RequiredArgsConstructor
public class SalaryAllowanceController {
    private final SalaryAllowanceService salaryAllowanceService;

    /**
     * Retrieves a specific salary allowance by its unique identifier.
     * 
     * <p>This endpoint allows authorized users to fetch detailed information about
     * a specific salary allowance. The service performs tenant isolation to ensure
     * users can only access allowances belonging to their tenant.</p>
     * 
     * @param salaryAllowanceId the unique identifier of the salary allowance to retrieve
     * @return ResponseFactory containing the salary allowance details if found
     * @throws ResourceNotFoundException if the salary allowance is not found or doesn't belong to the current tenant
     */
    @GetMapping("/{salaryAllowanceId}")
    @ResponseStatus(OK)
    public ResponseFactory<SalaryAllowanceResponse> findSalaryAllowance(
            @PathVariable UUID salaryAllowanceId
    ) {
        // Delegate to service layer which handles tenant isolation and business logic
        return ResponseFactory.success(
                "Find One Success",
                salaryAllowanceService.findById(salaryAllowanceId));
    }

    /**
     * Updates the effective dates of an existing salary allowance.
     * 
     * <p>This specialized endpoint allows modification of only the effective date range
     * for a salary allowance while preserving other attributes. This is particularly useful
     * for managing time-bound allowances or adjusting validity periods without recreating
     * the entire allowance record.</p>
     * 
     * <p>Complex validation includes:</p>
     * <ul>
     *   <li>Date range overlap prevention within the same employee</li>
     *   <li>Historical data integrity checks</li>
     *   <li>Tenant ownership verification</li>
     * </ul>
     * 
     * <p>Security: Restricted to roles with payroll management permissions.</p>
     * 
     * @param salaryAllowanceId the unique identifier of the allowance to update
     * @param updateRequest the validated request containing new effective date range
     * @return ResponseFactory with HTTP 200 status containing the updated allowance details
     * @throws ValidationException       if date validation rules are violated
     * @throws ResourceNotFoundException if the allowance doesn't exist or isn't accessible
     */
    @PutMapping("/{salaryAllowanceId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<SalaryAllowanceResponse> updateSalaryAllowanceDates(
            @PathVariable UUID salaryAllowanceId,
            @Valid @RequestBody SalaryAllowanceDatesUpdateRequest updateRequest
    ) {
        // Service layer handles complex date validation and business rules:
        // - Prevents overlapping date ranges for the same employee
        // - Validates historical data integrity
        // - Ensures tenant isolation
        return ResponseFactory.success(
                "Update Success",
                salaryAllowanceService.updateEffectiveDates(salaryAllowanceId, updateRequest)
        );
    }
}