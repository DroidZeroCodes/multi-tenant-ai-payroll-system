package org.droid.zero.multitenantaipayrollsystem.modules.salary.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryStructureRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryStructureResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.service.SalaryAllowanceService;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.service.SalaryStructureService;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.BusinessRuleException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ResourceNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ValidationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * REST Controller for managing salary structures in the multi-tenant AI payroll system.
 * 
 * <p>This controller provides comprehensive management of salary structures, which define
 * the complete compensation framework for employees including base salary, allowances,
 * deductions, and other compensation components. Salary structures serve as templates
 * that can be applied to multiple employees within the same tenant.</p>
 * 
 * <p>Key capabilities:</p>
 * <ul>
 *   <li>Create and manage salary structure templates</li>
 *   <li>Retrieve salary structure details with associated allowances</li>
 *   <li>Update salary structure configurations</li>
 *   <li>Toggle salary structure active/inactive status</li>
 *   <li>Access specific allowances within salary structures</li>
 * </ul>
 * 
 * <p>Security considerations: All modification operations require elevated privileges
 * due to the sensitive nature of compensation data and potential financial impact.</p>
 * 
 * @author Multi-Tenant AI Payroll System
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("${api.endpoint.base-url}/salary-structures")
@RequiredArgsConstructor
public class SalaryController {
    private final SalaryStructureService salaryStructureService;
    private final SalaryAllowanceService salaryAllowanceService;

    /**
     * Retrieves a specific salary structure by its unique identifier.
     * 
     * <p>This endpoint returns comprehensive details about a salary structure including
     * base salary information, associated allowances, and other compensation components.
     * The service ensures tenant isolation to prevent cross-tenant data access.</p>
     * 
     * <p>The returned structure includes all active allowances and their current values,
     * making it suitable for payroll calculations and employee compensation display.</p>
     * 
     * @param salaryId the unique identifier of the salary structure to retrieve
     * @return ResponseFactory containing the complete salary structure details
     * @throws ResourceNotFoundException if the salary structure is not found or doesn't belong to the current tenant
     */
    @GetMapping("/{salaryId}")
    @ResponseStatus(OK)
    public ResponseFactory<SalaryStructureResponse> findSalary(
            @PathVariable UUID salaryId
    ) {
        // Service layer handles tenant isolation and loads complete structure with allowances
        return ResponseFactory.success(
                "Find One Success",
                salaryStructureService.findById(salaryId));
    }

    /**
     * Creates a new salary structure in the system.
     * 
     * <p>This endpoint allows authorized personnel to create comprehensive salary structures
     * that define complete compensation frameworks. The creation process involves:</p>
     * <ul>
     *   <li>Validation of base salary and component values</li>
     *   <li>Business rule compliance (minimum wage, grade structures)</li>
     *   <li>Tenant-specific policy enforcement</li>
     *   <li>Allowance and deduction configuration</li>
     * </ul>
     * 
     * <p>Complex validation ensures data integrity and compliance with both system rules
     * and tenant-specific compensation policies.</p>
     * 
     * <p>Security: Requires payroll management privileges due to financial impact.</p>
     * 
     * @param salaryStructureRequest the validated request containing complete salary structure details
     * @return ResponseFactory with HTTP 201 status containing the created salary structure
     * @throws ValidationException if validation rules or business constraints are violated
     */
    @PostMapping()
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<SalaryStructureResponse> createSalary(
            @Valid @RequestBody SalaryStructureRequest salaryStructureRequest
    ) {
        // Service layer handles complex business logic:
        // - Multi-level validation (system, tenant, business rules)
        // - Allowance and deduction processing
        // - Tenant isolation and policy enforcement
        return ResponseFactory.created(
                "Create Success",
                salaryStructureService.save(salaryStructureRequest)
        );
    }

    /**
     * Updates an existing salary structure with new configuration.
     * 
     * <p>This endpoint allows comprehensive updates to salary structures while maintaining
     * data integrity and audit trails. The update process includes:</p>
     * <ul>
     *   <li>Validation of all modified components</li>
     *   <li>Historical data preservation for payroll calculations</li>
     *   <li>Impact assessment on existing employee assignments</li>
     *   <li>Business rule compliance checks</li>
     * </ul>
     * 
     * <p>Complex logic handles scenarios such as:</p>
     * <ul>
     *   <li>Updating base salary without affecting historical payroll</li>
     *   <li>Modifying allowances with effective date management</li>
     *   <li>Ensuring compliance with labor regulations</li>
     * </ul>
     * 
     * <p>Security: Restricted to roles with payroll modification privileges.</p>
     * 
     * @param salaryId the unique identifier of the salary structure to update
     * @param salaryStructureRequest the validated request containing updated structure details
     * @return ResponseFactory with HTTP 200 status containing the updated salary structure
     * @throws BusinessRuleException if updates would violate business constraints or regulations
     */
    @PutMapping("/{salaryId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<SalaryStructureResponse> updateSalary(
            @PathVariable UUID salaryId,
            @Valid @RequestBody SalaryStructureRequest salaryStructureRequest
    ) {
        // Service layer handles complex update logic:
        // - Historical data integrity preservation
        // - Employee assignment impact assessment
        // - Business rule validation and compliance checks
        return ResponseFactory.success(
                "Update Success",
                salaryStructureService.update(salaryStructureRequest, salaryId)
        );
    }

    /**
     * Toggles the active status of a salary structure.
     * 
     * <p>This specialized endpoint allows administrators to activate or deactivate salary structures
     * without modifying the underlying configuration. This is particularly useful for:</p>
     * <ul>
     *   <li>Seasonal compensation structures</li>
     *   <li>Temporary salary adjustments</li>
     *   <li>Phased rollout of new compensation plans</li>
     *   <li>Emergency suspension of specific structures</li>
     * </ul>
     * 
     * <p>Complex business logic includes:</p>
     * <ul>
     *   <li>Validation of current employee assignments</li>
     *   <li>Payroll period compatibility checks</li>
     *   <li>Audit trail maintenance for status changes</li>
     * </ul>
     * 
     * <p>Security: Requires payroll management privileges due to potential impact on employee compensation.</p>
     * 
     * @param salaryId the unique identifier of the salary structure to toggle
     * @return ResponseFactory with HTTP 200 status indicating successful status change
     * @throws BusinessRuleException if status change would interfere with active payroll processes
     */
    @PatchMapping("/{salaryId}/status")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<?> updateSalaryStatus(@PathVariable UUID salaryId) {
        // Service layer handles complex status toggle logic:
        // - Employee assignment validation
        // - Payroll period compatibility checks
        // - Audit trail generation
        salaryStructureService.toggleSalaryStatus(salaryId);
        return ResponseFactory.success(
                "Update Success",
                null
        );
    }

    /**
     * Retrieves a specific allowance within a salary structure.
     * 
     * <p>This nested endpoint provides access to individual allowances that are part of
     * a larger salary structure. This is particularly useful for:</p>
     * <ul>
     *   <li>Displaying specific allowance details in employee portals</li>
     *   <li>Allowance-specific calculations and reporting</li>
     *   <li>Granular access control for different compensation components</li>
     * </ul>
     * 
     * <p>The service performs complex validation to ensure:</p>
     * <ul>
     *   <li>The allowance belongs to the specified salary structure</li>
     *   <li>Both structure and allowance are accessible to the current tenant</li>
     *   <li>The allowance is currently active and effective</li>
     * </ul>
     * 
     * @param salaryId the unique identifier of the salary structure
     * @param allowanceId the unique identifier of the specific allowance within the structure
     * @return ResponseFactory containing the specific allowance details
     * @throws ResourceNotFoundException if either the structure or allowance is not found or not accessible
     */
    @GetMapping("/{salaryId}/allowances/{allowanceId}")
    @ResponseStatus(OK)
    public ResponseFactory<SalaryAllowanceResponse> findSalaryAllowance(
            @PathVariable UUID salaryId,
            @PathVariable UUID allowanceId
    ) {
        return ResponseFactory.success(
                "Find One Success",
                salaryAllowanceService.findBySalaryIdAndAllowanceId(salaryId, allowanceId));
    }

    /**
     * Creates a new salary allowance within a specific salary structure.
     *
     * <p>This nested endpoint allows authorized personnel (Super Admin, Tenant Admin, or Payroll Officer)
     * to create new salary allowances within a specified salary structure. The request undergoes comprehensive
     * validation including:</p>
     * <ul>
     *   <li>Business rule validation (amount ranges, allowance types)</li>
     *   <li>Tenant isolation checks</li>
     *   <li>Effective date validation</li>
     * </ul>
     *
     * <p>Security: Requires elevated privileges due to the financial nature of salary data.</p>
     *
     * @param salaryId the unique identifier of the salary structure
     * @param salaryAllowanceRequest the validated request containing allowance details
     * @return ResponseFactory with HTTP 201 status containing the created allowance details
     * @throws ValidationException if validation rules are violated
     */
    @PostMapping("/{salaryId}/allowances")
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<SalaryAllowanceResponse> createSalaryAllowance(
            @PathVariable UUID salaryId,
            @Valid @RequestBody SalaryAllowanceRequest salaryAllowanceRequest
    ) {
        // Service layer handles complex business logic including:
        // - Tenant isolation
        // - Duplicate allowance prevention
        // - Effective date validation
        return ResponseFactory.created(
                "Create Success",
                salaryAllowanceService.save(salaryId, salaryAllowanceRequest)
        );
    }
}