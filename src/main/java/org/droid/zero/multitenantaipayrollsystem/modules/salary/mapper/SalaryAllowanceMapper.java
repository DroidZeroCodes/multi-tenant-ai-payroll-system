package org.droid.zero.multitenantaipayrollsystem.modules.salary.mapper;

import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.SalaryAllowanceResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.SalaryAllowance;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface SalaryAllowanceMapper {

    SalaryAllowanceResponse toResponse(SalaryAllowance salaryAllowance);

}
