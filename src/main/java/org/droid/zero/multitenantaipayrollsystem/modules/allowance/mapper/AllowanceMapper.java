package org.droid.zero.multitenantaipayrollsystem.modules.allowance.mapper;

import org.droid.zero.multitenantaipayrollsystem.modules.allowance.dto.AllowanceResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.allowance.model.Allowance;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface AllowanceMapper {

    AllowanceResponse toResponse(Allowance allowance);

}
