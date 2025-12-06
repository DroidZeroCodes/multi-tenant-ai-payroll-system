package org.droid.zero.multitenantaipayrollsystem.tenant;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.system.api.ResponseFactory;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.CreateTenantRequest;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.endpoint.base-url}/tenants")
@RequiredArgsConstructor
public class TenantController {
    private final TenantService tenantService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    private ResponseFactory<TenantResponse> addTenant(@Valid @RequestBody CreateTenantRequest newTenant) {
        TenantResponse savedTenant = this.tenantService.save(newTenant);
        return ResponseFactory.created(
                "Tenant Creation Success",
                savedTenant
        );
    }
}