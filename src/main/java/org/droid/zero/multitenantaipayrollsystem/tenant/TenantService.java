package org.droid.zero.multitenantaipayrollsystem.tenant;

import org.droid.zero.multitenantaipayrollsystem.tenant.dto.CreateTenantRequest;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantResponse;

public interface TenantService {
    TenantResponse save(CreateTenantRequest request);
}
