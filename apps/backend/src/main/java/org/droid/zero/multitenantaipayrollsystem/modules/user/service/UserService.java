package org.droid.zero.multitenantaipayrollsystem.modules.user.service;

import org.droid.zero.multitenantaipayrollsystem.modules.tenant.events.TenantCreatedEvent;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

public interface UserService {
    UserResponse findById(UUID userId);

    UserResponse save(UserRegistrationRequest request);

    @TransactionalEventListener
    @Async
    void handleTenantCreatedEvent(TenantCreatedEvent event);
}
