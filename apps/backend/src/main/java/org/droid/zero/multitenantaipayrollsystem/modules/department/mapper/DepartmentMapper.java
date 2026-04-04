package org.droid.zero.multitenantaipayrollsystem.modules.department.mapper;

import org.droid.zero.multitenantaipayrollsystem.modules.department.dto.DepartmentResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.department.model.Department;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DepartmentMapper {

    DepartmentResponse toResponse(Department entity);

}
