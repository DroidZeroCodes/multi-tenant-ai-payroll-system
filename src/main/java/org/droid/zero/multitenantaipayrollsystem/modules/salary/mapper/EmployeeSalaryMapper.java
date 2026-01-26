package org.droid.zero.multitenantaipayrollsystem.modules.salary.mapper;

import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.EmployeeSalaryResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.EmployeeSalary;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface EmployeeSalaryMapper {

    EmployeeSalaryResponse toResponse(EmployeeSalary employeeSalary);

}
