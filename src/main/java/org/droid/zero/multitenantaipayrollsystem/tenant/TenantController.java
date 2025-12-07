package org.droid.zero.multitenantaipayrollsystem.tenant;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.CreateTenantRequest;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantResponse;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.UpdateTenantRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${api.endpoint.base-url}/tenants")
@RequiredArgsConstructor
public class TenantController {
    private final TenantService tenantService;

    @GetMapping("/{tenantId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseFactory<TenantResponse> findTenantById(@PathVariable() UUID tenantId) {
        return ResponseFactory.success(
                "Find One Success",
                tenantService.findById(tenantId)
        );
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    private ResponseFactory<TenantResponse> addTenant(@Valid @RequestBody CreateTenantRequest newTenant) {
        TenantResponse savedTenant = this.tenantService.save(newTenant);
        return ResponseFactory.created(
                "Create Success",
                savedTenant
        );
    }

    @PutMapping("/{tenantId}")
    @ResponseStatus(HttpStatus.OK)
    private ResponseFactory<TenantResponse> updateTenant(
            @Valid @RequestBody UpdateTenantRequest newTenant,
            @PathVariable UUID tenantId
    ) {
        TenantResponse savedTenant = this.tenantService.update(newTenant, tenantId);
        return ResponseFactory.success(
                "Update Success",
                savedTenant
        );
    }
}