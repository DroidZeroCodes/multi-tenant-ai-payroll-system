package org.droid.zero.multitenantaipayrollsystem.modules.position.mapper;

import org.droid.zero.multitenantaipayrollsystem.modules.position.dto.PositionResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.position.model.Position;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PositionMapper {

    PositionResponse toResponse(Position position);

}
