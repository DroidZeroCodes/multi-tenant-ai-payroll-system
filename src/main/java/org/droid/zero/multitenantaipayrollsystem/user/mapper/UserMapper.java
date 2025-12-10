package org.droid.zero.multitenantaipayrollsystem.user.mapper;

import org.droid.zero.multitenantaipayrollsystem.user.User;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserResponse toResponse(User user);

    List<UserResponse> toResponse(List<User> users);

    User toEntity(UserRegistrationRequest userRegistrationRequest);
}
