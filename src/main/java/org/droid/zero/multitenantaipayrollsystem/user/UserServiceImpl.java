package org.droid.zero.multitenantaipayrollsystem.user;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.security.auth.dto.CredentialsRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.security.auth.mapper.UserCredentialsMapper;
import org.droid.zero.multitenantaipayrollsystem.security.auth.user.credentials.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.droid.zero.multitenantaipayrollsystem.tenant.Tenant;
import org.droid.zero.multitenantaipayrollsystem.tenant.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.tenant.events.TenantCreatedEvent;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserResponse;
import org.droid.zero.multitenantaipayrollsystem.user.mapper.UserMapper;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.TENANT;
import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.USER;
import static org.droid.zero.multitenantaipayrollsystem.user.UserRole.TENANT_ADMIN;


@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final UserMapper userMapper;
    private final UserCredentialsMapper credentialsMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse findById(UUID userId) {
        //Find the User by its ID then map it to the response object if exists, if not, throw an exception
        return this.userRepository.findById(userId)
                .map(userMapper::toResponse)
                .orElseThrow(()-> new ObjectNotFoundException(USER, userId));
    }

    @Override
    public User findByEmail(String email) {
        //Find the User by its email then throw exception if not exists
        return this.userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(()-> new ObjectNotFoundException(USER, email, "email"));
    }

    @Override
    public UserResponse save(UserRegistrationRequest request) {
        //Validate the provided credentials

        //Confirm that the new password and confirm password does match
        if (!Objects.equals(request.credentials().password(), request.credentials().confirmPassword()))
            throw new IllegalArgumentException("passwords don't match");

        //Check if the tenant exists and throw an exception when not found
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(()-> new ObjectNotFoundException(TENANT, request.tenantId()));

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
        User savedUser = this.userRepository.save(user);

        //Map the saved model to a response object, then return
        return userMapper.toResponse(savedUser);
    }

    @EventListener
    @Async
    @Override
    public void handleTenantCreatedEvent(TenantCreatedEvent event) {
        // Create an admin user for the new tenant
        save(
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
}
