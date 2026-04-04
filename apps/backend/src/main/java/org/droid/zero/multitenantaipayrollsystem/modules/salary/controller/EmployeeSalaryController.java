package org.droid.zero.multitenantaipayrollsystem.modules.salary.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.EmployeeSalaryChangeRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.EmployeeSalaryRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.EmployeeSalaryResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.service.EmployeeSalaryService;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.BusinessRuleException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.DuplicateResourceException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ResourceNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ValidationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * REST Controller for managing employee salary assignments in a multi-tenant payroll system.
 * 
 * <p>This controller handles CRUD operations for employee salary records, including:
 * <ul>
 *   <li>Creating new salary assignments</li>
 *   <li>Updating existing salary information</li>
 *   <li>Managing effective dates for salary changes</li>
 *   <li>Retrieving salary information by various criteria</li>
 * </ul>
 * 
 * <p><strong>Security Considerations:</strong>
 * All modification operations require elevated permissions (SUPER_ADMIN, TENANT_ADMIN, or PAYROLL_OFFICER)
 * to ensure only authorized personnel can modify sensitive salary data.
 * 
 * <p><strong>Multi-Tenant Support:</strong>
 * The controller operates within a multi-tenant context where all operations are automatically
 * scoped to the current tenant's data through the service layer.
 * 
 * @author Multi-Tenant AI Payroll System
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("${api.endpoint.base-url}/salary-assignments")
@RequiredArgsConstructor
public class EmployeeSalaryController {
    
    /**
     * Service layer for handling employee salary business logic.
     * Provides abstraction for database operations and business rule enforcement.
     */
    private final EmployeeSalaryService employeeSalaryService;

    /**
     * Retrieves employee salary information by its unique identifier.
     * 
     * <p>This endpoint provides direct access to a specific salary assignment record.
     * The operation is read-only and accessible to all authenticated users within the tenant.
     * 
     * <p><strong>Business Logic:</strong>
     * The service layer ensures that only salary records belonging to the current tenant
     * are returned, maintaining data isolation in the multi-tenant environment.
     *
     * @param employeeSalaryId The unique identifier of the employee salary record to retrieve.
     *                        Must be a valid UUID format.
     * @return ResponseFactory containing the employee salary information with HTTP 200 status.
     * @throws ResourceNotFoundException 
     *         if the salary record is not found or doesn't belong to the current tenant.
     */
    @GetMapping("/{employeeSalaryId}")
    @ResponseStatus(OK)
    public ResponseFactory<EmployeeSalaryResponse> findEmployeeSalary(
            @PathVariable UUID employeeSalaryId
    ) {
        // Delegate to service layer which handles tenant isolation and validation
        return ResponseFactory.success(
                "Find One Success",
                employeeSalaryService.findById(employeeSalaryId));
    }

    /**
     * Retrieves employee salary information for a specific employee on a given date.
     * 
     * <p>This endpoint handles the complex business logic of determining which salary assignment
     * was active for an employee on a specific date. This is crucial for:
     * <ul>
     *   <li>Historical payroll calculations</li>
     *   <li>Audit trail generation</li>
     *   <li>Salary change impact analysis</li>
     * </ul>
     * 
     * <p><strong>Complex Business Logic:</strong>
     * The service layer implements date-based salary resolution logic that considers:
     * <ul>
     *   <li>Effective date ranges</li>
     *   <li>Overlapping salary assignments</li>
     *   <li>Gaps in salary coverage</li>
     * </ul>
     *
     * @param employeeId The unique identifier of the employee whose salary is being queried.
     * @param date The specific date for which to find the active salary assignment.
     *              Used to determine which salary was in effect on that date.
     * @return ResponseFactory containing the employee salary information that was active on the specified date.
     * @throws ResourceNotFoundException  if no salary assignment is found for the employee on the given date.
     * @throws DuplicateResourceException if there are conflicting or invalid salary assignments.
     */
    @GetMapping("/employee/{employeeId}")
    @ResponseStatus(OK)
    public ResponseFactory<EmployeeSalaryResponse> findEmployeeSalaryOnDate(
            @PathVariable UUID employeeId,
            @RequestParam LocalDate date
    ) {
        // Service layer handles complex date-based salary resolution logic
        // including effective date range validation and conflict detection
        return ResponseFactory.success(
                "Find One Success",
                employeeSalaryService.findByEmployeeIdAndDate(employeeId, date));
    }

    /**
     * Creates a new employee salary assignment with comprehensive validation.
     * 
     * <p>This endpoint handles the complex process of creating new salary assignments,
     * which involves multiple business rules and validations:
     * <ul>
     *   <li>Preventing overlapping salary periods for the same employee</li>
     *   <li>Validating salary amount ranges and currency</li>
     *   <li>Ensuring effective date logic consistency</li>
     *   <li>Maintaining audit trail for compliance</li>
     * </ul>
     * 
     * <p><strong>Security & Authorization:</strong>
     * Restricted to users with payroll management permissions to prevent unauthorized
     * salary modifications that could impact payroll calculations and compliance.
     * 
     * <p><strong>Business Logic Complexity:</strong>
     * The service layer implements sophisticated validation including:
     * <ul>
     *   <li>Salary range validation against company policies</li>
     *   <li>Effective date conflict detection</li>
     *   <li>Currency and payment method validation</li>
     *   <li>Integration with employee status verification</li>
     * </ul>
     *
     * @param employeeSalaryRequest The request containing comprehensive salary assignment details
     *                             including employee ID, salary amount, effective date, payment frequency,
     *                             and other relevant metadata. Must pass validation constraints.
     * @return ResponseFactory containing the newly created employee salary information with HTTP 201 status.
     * @throws ValidationException        if the request fails validation or business rule checks.
     * @throws DuplicateResourceException if there are conflicts with existing salary assignments.
     * @throws ResourceNotFoundException  if the referenced employee doesn't exist or isn't active.
     */
    @PostMapping()
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<EmployeeSalaryResponse> createEmployeeSalary(
            @Valid @RequestBody EmployeeSalaryRequest employeeSalaryRequest
    ) {
        // Service layer handles complex business logic including:
        // - Overlap detection with existing salary assignments
        // - Salary range validation against company policies
        // - Effective date logic and conflict resolution
        // - Audit trail creation for compliance tracking
        return ResponseFactory.created(
                "Create Success",
                employeeSalaryService.save(employeeSalaryRequest)
        );
    }

    /**
     * Updates an existing employee salary assignment with comprehensive business logic.
     * 
     * <p>This endpoint handles salary modifications which is a critical operation requiring
     * extensive validation and business rule enforcement:
     * <ul>
     *   <li>Salary change approval workflow validation</li>
     *   <li>Historical data integrity preservation</li>
     *   <li>Payroll calculation impact assessment</li>
     *   <li>Compliance and audit trail maintenance</li>
     * </ul>
     * 
     * <p><strong>Complex Business Logic:</strong>
     * The service layer implements sophisticated salary change logic including:
     * <ul>
     *   <li>Creating new salary assignments with future effective dates</li>
     *   <li>Preserving historical salary data for accurate payroll reporting</li>
     *   <li>Validating salary change percentages against company policies</li>
     *   <li>Handling retroactive salary changes with proper calculations</li>
     * </ul>
     * 
     * <p><strong>Security Considerations:</strong>
     * This operation requires elevated permissions due to its direct impact on
     * payroll calculations and financial reporting.
     *
     * @param employeeSalaryId The unique identifier of the salary assignment to modify.
     *                         Must exist and belong to the current tenant.
     * @param request The request containing updated salary information including new salary amount,
     *                change reason, effective date for the change, and approval details.
     *                Must pass validation constraints.
     * @return ResponseFactory containing the updated employee salary information with HTTP 200 status.
     * @throws ValidationException        if the salary change violates business rules or validation constraints.
     * @throws DuplicateResourceException if the salary change conflicts with existing assignments or payroll periods.
     * @throws ResourceNotFoundException  if the salary assignment doesn't exist.
     */
    @PutMapping("/{employeeSalaryId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<EmployeeSalaryResponse> changeEmployeeSalary(
            @PathVariable UUID employeeSalaryId,
            @Valid @RequestBody EmployeeSalaryChangeRequest request
    ) {
        // Service layer handles complex salary change logic:
        // - Creates new salary assignment preserving historical data
        // - Validates change reasons and approval requirements
        // - Handles retroactive changes with proper calculations
        // - Maintains audit trail for compliance reporting
        return ResponseFactory.success(
                "Update Success",
                employeeSalaryService.changeSalary(employeeSalaryId, request)
        );
    }

    /**
     * Updates the effective date of an existing employee salary assignment.
     * 
     * <p>This endpoint handles the critical operation of modifying when a salary assignment
     * takes effect, which has significant implications for:
     * <ul>
     *   <li>Payroll calculation accuracy</li>
     *   <li>Historical reporting integrity</li>
     *   <li>Salary change timeline management</li>
     *   <li>Compliance with labor regulations</li>
     * </ul>
     * 
     * <p><strong>Complex Business Logic:</strong>
     * The service layer implements sophisticated effective date management:
     * <ul>
     *   <li>Preventing date conflicts with other salary assignments</li>
     *   <li>Validating that effective dates don't create gaps in coverage</li>
     *   <li>Ensuring historical payroll calculations remain accurate</li>
     *   <li>Managing retroactive date changes with proper notifications</li>
     * </ul>
     * 
     * <p><strong>Security & Compliance:</strong>
     * Requires payroll management permissions due to the potential impact on
     * financial calculations and regulatory compliance.
     *
     * @param employeeSalaryId The unique identifier of the salary assignment to modify.
     *                         Must exist and belong to the current tenant.
     * @param effectiveDate The new effective date when this salary assignment should take effect.
     *                      Must be a valid date that doesn't conflict with existing assignments.
     *                      Cannot be null as validated by the @NotNull constraint.
     * @return ResponseFactory containing the updated employee salary information with HTTP 200 status.
     * @throws ValidationException       if the effective date violates business rules.
     * @throws BusinessRuleException    if the date change would impact processed payroll periods.
     * @throws ResourceNotFoundException if the salary assignment doesn't exist.
     */
    @PatchMapping("/{employeeSalaryId}/effective-date")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<EmployeeSalaryResponse> updateEmployeeSalaryEffectiveDate(
            @PathVariable UUID employeeSalaryId,
            @Valid @RequestBody @NotNull(message = "Effective date is required") LocalDate effectiveDate
    ) {
        // Service layer handles complex effective date logic:
        // - Validates against existing salary assignments to prevent overlaps
        // - Ensures no gaps in salary coverage are created
        // - Checks impact on processed payroll periods
        // - Maintains audit trail for compliance purposes
        return ResponseFactory.success(
                "Update Success",
                employeeSalaryService.updateEffectiveDate(employeeSalaryId, effectiveDate)
        );
    }

    /**
     * Updates the end date of an existing employee salary assignment.
     * 
     * <p>This endpoint manages the termination or expiration of salary assignments,
     * which is critical for maintaining accurate payroll timelines:
     * <ul>
     *   <li>Employee termination processing</li>
     *   <li>Salary change transitions</li>
     *   <li>Contract expiration handling</li>
     *   <li>Payroll period boundary management</li>
     * </ul>
     * 
     * <p><strong>Complex Business Logic:</strong>
     * The service layer implements comprehensive end date management:
     * <ul>
     *   <li>Validating end date chronology (must be after effective date)</li>
     *   <li>Ensuring continuous salary coverage for active employees</li>
     *   <li>Handling overlapping assignments during transitions</li>
     *   <li>Managing final payroll calculations for terminated assignments</li>
     * </ul>
     * 
     * <p><strong>Business Impact:</strong>
     * This operation affects payroll calculations, reporting, and compliance.
     * Requires careful validation to prevent gaps in salary coverage.
     *
     * @param employeeSalaryId The unique identifier of the salary assignment to modify.
     *                         Must exist and belong to the current tenant.
     * @param endDate The new end date when this salary assignment should expire.
     *                Must be a valid date that comes after the effective date.
     *                Cannot be null as validated by the @NotNull constraint.
     * @return ResponseFactory containing the updated employee salary information with HTTP 200 status.
     * @throws ValidationException       if the end date creates gaps in coverage or violates business rules.
     * @throws BusinessRuleException    if the end date would impact processed payroll calculations.
     * @throws ResourceNotFoundException if the salary assignment doesn't exist.
     */
    @PatchMapping("/{employeeSalaryId}/end-date")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'PAYROLL_OFFICER')")
    public ResponseFactory<EmployeeSalaryResponse> updateEmployeeSalaryEndDate(
            @PathVariable UUID employeeSalaryId,
            @Valid @RequestBody @NotNull(message = "End date is required") LocalDate endDate
    ) {
        // Service layer handles complex end date logic:
        // - Validates chronological order (end date must be after effective date)
        // - Ensures no gaps in salary coverage for active employees
        // - Checks impact on processed payroll periods
        // - Manages transitions between salary assignments
        return ResponseFactory.success(
                "Update Success",
                employeeSalaryService.updateEndDate(employeeSalaryId, endDate)
        );
    }
}

