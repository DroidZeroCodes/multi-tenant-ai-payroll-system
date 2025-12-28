package org.droid.zero.multitenantaipayrollsystem.modules.tenant.mapper;

import org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto.TenantResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TenantMapper {
    TenantResponse toResponse(Tenant tenant);

    List<TenantResponse> toResponse(List<Tenant> tenants);
}
