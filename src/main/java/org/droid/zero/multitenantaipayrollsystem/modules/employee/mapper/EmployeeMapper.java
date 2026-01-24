package org.droid.zero.multitenantaipayrollsystem.modules.employee.mapper;

import org.droid.zero.multitenantaipayrollsystem.modules.employee.dto.EmployeeResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.employee.model.Employee;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface EmployeeMapper {

    EmployeeResponse toResponse(Employee employee);

}
