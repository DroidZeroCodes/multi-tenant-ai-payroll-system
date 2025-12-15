package org.droid.zero.multitenantaipayrollsystem.modules.tenant;

import org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto.TenantRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto.TenantResponse;

import java.util.UUID;

public interface TenantService {
    Tenant findById(UUID tenantId);
    TenantResponse findByIdResponse(UUID tenantId);
    TenantResponse save(TenantRequest request);
    TenantResponse update(TenantRequest request, UUID tenantId);
    boolean toggleTenantStatus(UUID tenantId);
}
