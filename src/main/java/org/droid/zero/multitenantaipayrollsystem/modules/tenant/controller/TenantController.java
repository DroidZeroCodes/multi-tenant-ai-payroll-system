package org.droid.zero.multitenantaipayrollsystem.modules.tenant.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto.TenantRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto.TenantResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.service.TenantService;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${api.endpoint.base-url}/tenants")
@RequiredArgsConstructor
public class TenantController {
    private final TenantService tenantService;

    @GetMapping("/{tenantId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseFactory<TenantResponse> findTenantById(@PathVariable() UUID tenantId) {
        return ResponseFactory.success(
                "Find One Success",
                tenantService.findById(tenantId)
        );
    }

    @GetMapping("/current")
    @ResponseStatus(HttpStatus.OK)
    public ResponseFactory<TenantResponse> findCurrentTenant() {
        return ResponseFactory.success(
                "Find One Success",
                tenantService.findCurrent()
        );
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseFactory<TenantResponse> addTenant(@Valid @RequestBody TenantRequest newTenant) {
        TenantResponse savedTenant = this.tenantService.save(newTenant);
        return ResponseFactory.created(
                "Create Success",
                savedTenant
        );
    }

    @PutMapping("/{tenantId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TENANT_ADMIN')")
    public ResponseFactory<TenantResponse> updateTenant(
            @Valid @RequestBody TenantRequest newTenant,
            @PathVariable UUID tenantId
    ) {
        TenantResponse savedTenant = this.tenantService.update(newTenant, tenantId);
        return ResponseFactory.success(
                "Update Success",
                savedTenant
        );
    }

    @PatchMapping("/{tenantId}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseFactory<?> updateTenantStatus(@PathVariable UUID tenantId) {
        this.tenantService.toggleTenantStatus(tenantId);
        return ResponseFactory.success(
                "Update Success",
                null
        );
    }
}

