package org.droid.zero.multitenantaipayrollsystem.user;

import org.droid.zero.multitenantaipayrollsystem.user.dto.UserRequest;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse findById(UUID userId);
    UserResponse save(UserRequest request);
}
