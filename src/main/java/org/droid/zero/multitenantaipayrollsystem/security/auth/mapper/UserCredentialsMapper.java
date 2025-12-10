package org.droid.zero.multitenantaipayrollsystem.security.auth.mapper;

import org.droid.zero.multitenantaipayrollsystem.security.auth.dto.CredentialsRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.security.auth.user.credentials.UserCredentials;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserCredentialsMapper {
    UserCredentials toEntity(CredentialsRegistrationRequest credentialsRegistrationRequest);
}
