package org.droid.zero.multitenantaipayrollsystem.tenant.mapper;

import org.droid.zero.multitenantaipayrollsystem.tenant.Tenant;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantRequest;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TenantMapper {
    TenantResponse toResponse(Tenant tenant);

    List<TenantResponse> toResponse(List<Tenant> tenants);

    Tenant toEntity(TenantRequest tenantRequest);
}
