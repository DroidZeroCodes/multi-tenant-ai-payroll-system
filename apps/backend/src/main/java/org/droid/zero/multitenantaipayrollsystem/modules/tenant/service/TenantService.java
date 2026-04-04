package org.droid.zero.multitenantaipayrollsystem.modules.tenant.service;

import org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto.TenantRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto.TenantResponse;

import java.util.UUID;

public interface TenantService {
    TenantResponse findById(UUID tenantId);

    TenantResponse findCurrent();

    TenantResponse save(TenantRequest request);

    TenantResponse update(TenantRequest request, UUID tenantId);

    void toggleTenantStatus(UUID tenantId);
}
