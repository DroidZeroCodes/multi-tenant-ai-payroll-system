package org.droid.zero.multitenantaipayrollsystem.modules.user.mapper;

import org.droid.zero.multitenantaipayrollsystem.modules.user.User;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserResponse toResponse(User user);

    List<UserResponse> toResponse(List<User> users);

    @Mappings(
            @Mapping(target = "email", source = "credentials.email")
    )
    User toEntity(UserRegistrationRequest userRegistrationRequest);
}
