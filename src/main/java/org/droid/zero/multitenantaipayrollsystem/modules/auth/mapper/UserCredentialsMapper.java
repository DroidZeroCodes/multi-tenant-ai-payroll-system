package org.droid.zero.multitenantaipayrollsystem.modules.auth.mapper;

import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.CredentialsRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.UserCredentials;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserCredentialsMapper {
    UserCredentials toEntity(CredentialsRegistrationRequest credentialsRegistrationRequest);
}
