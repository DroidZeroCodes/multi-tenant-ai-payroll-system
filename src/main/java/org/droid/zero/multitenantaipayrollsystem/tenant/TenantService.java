package org.droid.zero.multitenantaipayrollsystem.tenant;

import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantRequest;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantResponse;

import java.util.UUID;

public interface TenantService {
    TenantResponse findById(UUID tenantId);
    TenantResponse save(TenantRequest request);
    TenantResponse update(TenantRequest request, UUID tenantId);
    boolean toggleTenantStatus(UUID tenantId);
}
