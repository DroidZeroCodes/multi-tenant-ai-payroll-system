package org.droid.zero.multitenantaipayrollsystem.modules.deduction.mapper;

import org.droid.zero.multitenantaipayrollsystem.modules.deduction.dto.DeductionResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.deduction.model.Deduction;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DeductionMapper {

    DeductionResponse toResponse(Deduction deduction);

}
