package org.droid.zero.multitenantaipayrollsystem.modules.user.mapper;

import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.mapstruct.Mapper;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserMapper {
    UserResponse toResponse(User user);

    List<UserResponse> toResponse(List<User> users);
}
