package org.droid.zero.multitenantaipayrollsystem.user;

import org.droid.zero.multitenantaipayrollsystem.tenant.events.TenantCreatedEvent;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserResponse;
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
