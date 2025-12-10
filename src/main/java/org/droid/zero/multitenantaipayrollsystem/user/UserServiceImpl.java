package org.droid.zero.multitenantaipayrollsystem.user;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.security.auth.mapper.UserCredentialsMapper;
import org.droid.zero.multitenantaipayrollsystem.security.auth.user.credentials.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.FieldDuplicateValidator;
import org.droid.zero.multitenantaipayrollsystem.tenant.Tenant;
import org.droid.zero.multitenantaipayrollsystem.tenant.TenantService;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserResponse;
import org.droid.zero.multitenantaipayrollsystem.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.USER;


@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TenantService tenantService;
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

        //Check if the tenant exists using the tenant service, it already throws an exception when not found
        Tenant tenant = tenantService.findById(request.tenantId());

        //Validate if the provided arguments does not violate unique constraints
        new FieldDuplicateValidator()
                .addField(userRepository.existsByEmailIgnoreCaseAndTenantId(request.credentials().email(), request.tenantId()), "email")
                .validate(USER);

        //Convert request to an entity to be persisted and set the tenant
        User user = userMapper.toEntity(request);
        user.setTenant(tenant);

        //Create the new user record in the database
        User savedUser = this.userRepository.save(user);

        //Create the credentials and map the newly created user, then set the hashed password and tenant
        UserCredentials userCredentials = credentialsMapper.toEntity(request.credentials());
        userCredentials.setPassword(passwordEncoder.encode(request.credentials().password()));
        userCredentials.setTenant(tenant);

        //Map the saved model to a response object, then return
        return userMapper.toResponse(savedUser);
    }
}
