package org.droid.zero.multitenantaipayrollsystem.modules.user.service;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.CredentialsRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.model.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.events.TenantCreatedEvent;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.repository.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.user.mapper.UserMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.modules.user.repository.UserRepository;
import org.droid.zero.multitenantaipayrollsystem.system.BaseService;
import org.droid.zero.multitenantaipayrollsystem.system.TenantExecutor;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole.TENANT_ADMIN;
import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.TENANT;
import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.USER;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseService implements UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TenantExecutor tenantExecutor;

    @Transactional
    @Override
    public UserResponse findById(UUID userId) {
        //Fetch the requested user (or fail with 404)
        User requestedUser = userRepository.findByIdAndUserTenantRoles_TenantId(userId, TenantContext.getTenantId())
                .orElseThrow(() -> new ObjectNotFoundException(USER, userId));


        return userMapper.toResponse(requestedUser);
    }

    @Transactional
    @Override
    public UserResponse save(UserRegistrationRequest request) {
        //Confirm that the new password and confirm password does match
        if (!Objects.equals(request.credentials().password(), request.credentials().confirmPassword()))
            throw new IllegalArgumentException("passwords don't match");

        // Delegate to an internal method (Reused by Event Listener)
        User savedUser = createUser(request, TenantContext.getTenantId());

        return userMapper.toResponse(savedUser);
    }

    @TransactionalEventListener
    @Async
    @Override
    public void handleTenantCreatedEvent(TenantCreatedEvent event) {
        // Create an admin user for the new tenant
        tenantExecutor.runAsTenant(event.tenantId(), () -> createUser(
                new UserRegistrationRequest(
                        event.name(),
                        "Admin",
                        event.email(),
                        Set.of(TENANT_ADMIN),
                        new CredentialsRegistrationRequest(
                                event.email(),
                                event.generatedPassword(),
                                event.generatedPassword()
                        )
                ),
                event.tenantId()
        ));
    }

    private User createUser(UserRegistrationRequest request, UUID tenantId) {
        if (!tenantRepository.existsById(tenantId)) throw new ObjectNotFoundException(TENANT, tenantId, "tenantId");

        //Validate if the provided arguments does not violate unique constraints
        userRepository.findByContactEmail(request.contactEmail()).ifPresent(user -> new FieldDuplicateValidator()
                .addField(user.getTenantIds().contains(tenantId), "contactEmail")
                .validate(USER));

        //Convert request to an entity to be persisted and set the tenant
        User user = new User(
                request.firstName(),
                request.lastName(),
                request.contactEmail(),
                request.roles(),
                new UserCredentials(
                        request.credentials().email(),
                        passwordEncoder.encode(request.credentials().password()
                )),
                tenantId
        );

        //Create the new user record in the database
        return this.userRepository.save(user);
    }
}
