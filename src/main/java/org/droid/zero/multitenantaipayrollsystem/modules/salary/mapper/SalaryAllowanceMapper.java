package org.droid.zero.multitenantaipayrollsystem.modules.salary.mapper;

import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.SalaryAllowance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface SalaryAllowanceMapper {

    @Mapping(target = "salaryStructureId", source = "salaryStructure.id")
    @Mapping(target = "allowanceId", source = "allowance.id")
    SalaryAllowanceResponse toResponse(SalaryAllowance salaryAllowance);

}
