package org.droid.zero.multitenantaipayrollsystem.modules.salary.mapper;

import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryStructureResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.SalaryStructure;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface SalaryStructureMapper {

    SalaryStructureResponse toResponse(SalaryStructure salaryStructure);

}
