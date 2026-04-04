package org.droid.zero.multitenantaipayrollsystem.modules.department.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.department.dto.DepartmentRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.department.dto.DepartmentResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.department.service.DepartmentService;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("${api.endpoint.base-url}/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping("/{departmentId}")
    @ResponseStatus(OK)
    public ResponseFactory<DepartmentResponse> findDepartment(
            @PathVariable UUID departmentId
    ) {
        return ResponseFactory.success(
                "Find One Success",
                departmentService.findById(departmentId));
    }

    @PostMapping()
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'HR_OFFICER')")
    public ResponseFactory<DepartmentResponse> createDepartment(
            @Valid @RequestBody DepartmentRequest departmentRequest
    ) {
        return ResponseFactory.created(
                "Create Success",
                departmentService.save(departmentRequest)
        );
    }

    @PutMapping("/{departmentId}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'HR_OFFICER')")
    public ResponseFactory<DepartmentResponse> updateDepartment(
            @PathVariable UUID departmentId,
            @Valid @RequestBody DepartmentRequest departmentRequest
    ) {
        return ResponseFactory.success(
                "Update Success",
                departmentService.update(departmentRequest, departmentId)
        );
    }

    @PatchMapping("/{departmentId}/status")
    @ResponseStatus(OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN', 'HR_OFFICER')")
    public ResponseFactory<?> updateDepartmentStatus(@PathVariable UUID departmentId) {
        departmentService.toggleDepartmentStatus(departmentId);
        return ResponseFactory.success(
                "Update Success",
                null
        );
    }
}