package org.droid.zero.multitenantaipayrollsystem.modules.user;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.CredentialsRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.mapper.UserCredentialsMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.events.TenantCreatedEvent;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.Tenant;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.repository.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.user.mapper.UserMapper;
import org.droid.zero.multitenantaipayrollsystem.system.BaseService;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.modules.user.UserRole.SUPER_ADMIN;
import static org.droid.zero.multitenantaipayrollsystem.modules.user.UserRole.TENANT_ADMIN;
import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.TENANT;
import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.USER;


@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl extends BaseService implements UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final UserMapper userMapper;
    private final UserCredentialsMapper credentialsMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse findById(UUID userId) {
        //Get the current authenticated credentials
        UserCredentials currentUser = (UserCredentials) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        //Fetch the requested user (or fail with 404)
        User requestedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(USER, userId));

        checkReadPermission(currentUser, requestedUser);

        return userMapper.toResponse(requestedUser);
    }

    @Override
    public User findByEmail(String email) {
        //Get the current authenticated credentials
        UserCredentials currentUser = (UserCredentials) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        //Fetch the requested user (or fail with 404)
        User requestedUser = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ObjectNotFoundException(USER, email, "email"));

        checkReadPermission(currentUser, requestedUser);

        return requestedUser;
    }

    @Override
    public UserResponse save(UserRegistrationRequest request) {
        UserCredentials currentUser = getCurrentUser();

        // Tenant Admin can only create users in their own tenant
        if (currentUser.getRole().contains(TENANT_ADMIN)) {
            if (!request.tenantId().equals(currentUser.getTenant().getId())) {
                throw new AccessDeniedException("You cannot create users for other tenants.");
            }
        }

        //Confirm that the new password and confirm password does match
        if (!Objects.equals(request.credentials().password(), request.credentials().confirmPassword()))
            throw new IllegalArgumentException("passwords don't match");

        //Check if the tenant exists and throw an exception when not found
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(()-> new ObjectNotFoundException(TENANT, request.tenantId()));

        // Delegate to an internal method (Reused by Event Listener)
        User savedUser = createUser(request);

        return userMapper.toResponse(savedUser);
    }

    @EventListener
    @Async
    @Override
    public void handleTenantCreatedEvent(TenantCreatedEvent event) {
        // Create an admin user for the new tenant
        createUser(
                new UserRegistrationRequest(
                        event.name(),
                        "Admin",
                        new CredentialsRegistrationRequest(
                                event.email(),
                                event.generatedPassword(),
                                event.generatedPassword(),
                                Set.of(TENANT_ADMIN)
                        ),
                        event.tenantId()
                )
        );
    }

    private User createUser(UserRegistrationRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(()-> new ObjectNotFoundException(TENANT, request.tenantId(), "tenantId"));

        //Validate if the provided arguments does not violate unique constraints
        new FieldDuplicateValidator()
                .addField(userRepository.existsByEmailIgnoreCaseAndTenantId(request.credentials().email(), request.tenantId()), "email")
                .validate(USER);

        //Convert request to an entity to be persisted and set the tenant
        User user = userMapper.toEntity(request);
        user.setTenant(tenant);

        //Create the credentials and map the newly created user, then set the hashed password and tenant
        UserCredentials userCredentials = credentialsMapper.toEntity(request.credentials());
        userCredentials.setPassword(passwordEncoder.encode(request.credentials().password()));
        userCredentials.setTenant(tenant);

        //Assign the credentials to the user and create the new user record in the database
        user.setUserCredentials(userCredentials);
        return this.userRepository.save(user);
    }

    private void checkReadPermission(UserCredentials currentUser, User targetUser) {
        // 1. Super Admin: Allow everything
        if (currentUser.getRole().contains(SUPER_ADMIN)) return;

        // 2. Tenant Admin: Allow if in same tenant
        if (currentUser.getRole().contains(TENANT_ADMIN)) {
            if (!targetUser.getTenant().getId().equals(currentUser.getTenant().getId())) {
                throwAccessDenied();
            }
            return;
        }

        // 3. Regular User: Allow ONLY if accessing self
        if (!currentUser.getUser().getId().equals(targetUser.getId())) {
            throwAccessDenied();
        }
    }
}
