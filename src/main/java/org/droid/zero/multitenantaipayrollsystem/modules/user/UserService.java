package org.droid.zero.multitenantaipayrollsystem.modules.user;

import org.droid.zero.multitenantaipayrollsystem.modules.tenant.events.TenantCreatedEvent;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserResponse;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.UUID;

public interface UserService {
    UserResponse findById(UUID userId);

    User findByEmail(String email);

    UserResponse save(UserRegistrationRequest request);

    @EventListener
    @Async
    void handleTenantCreatedEvent(TenantCreatedEvent event);
}
