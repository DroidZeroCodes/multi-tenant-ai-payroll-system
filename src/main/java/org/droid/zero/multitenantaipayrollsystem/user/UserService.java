package org.droid.zero.multitenantaipayrollsystem.user;

import org.droid.zero.multitenantaipayrollsystem.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse findById(UUID userId);
    User findByEmail(String email);
    UserResponse save(UserRegistrationRequest request);
}
