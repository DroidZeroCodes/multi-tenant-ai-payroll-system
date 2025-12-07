package org.droid.zero.multitenantaipayrollsystem.tenant;

import org.droid.zero.multitenantaipayrollsystem.tenant.dto.CreateTenantRequest;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantResponse;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.UpdateTenantRequest;

import java.util.UUID;

public interface TenantService {
    TenantResponse findById(UUID tenantId);
    TenantResponse save(CreateTenantRequest request);
    TenantResponse update(UpdateTenantRequest request, UUID tenantId);
}
