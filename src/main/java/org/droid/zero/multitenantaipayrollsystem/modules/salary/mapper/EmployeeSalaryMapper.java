package org.droid.zero.multitenantaipayrollsystem.modules.salary.mapper;

import org.droid.zero.multitenantaipayrollsystem.modules.salary.dto.EmployeeSalaryResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.salary.model.EmployeeSalary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface EmployeeSalaryMapper {

    @Mapping(target = "salaryStructureId", source = "salaryStructure.id")
    @Mapping(target = "employeeId", source = "employee.id")
    EmployeeSalaryResponse toResponse(EmployeeSalary employeeSalary);

}
