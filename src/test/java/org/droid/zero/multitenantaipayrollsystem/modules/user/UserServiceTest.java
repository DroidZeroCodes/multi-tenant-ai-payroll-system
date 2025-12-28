package org.droid.zero.multitenantaipayrollsystem.modules.user;

import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.CredentialsRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.model.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.Tenant;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.repository.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.user.mapper.UserMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.modules.user.repository.UserRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.service.UserServiceImpl;
import org.droid.zero.multitenantaipayrollsystem.system.TenantExecutor;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.DuplicateResourceException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.test.config.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole.EMPLOYEE;
import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends BaseUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock TenantExecutor tenantExecutor;

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private UserServiceImpl userService;

    private User user;
    private UserRegistrationRequest userRegistrationRequest;
    private UUID userId;
    private UUID tenantId;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userRepository,
                tenantRepository,
                userMapper,
                passwordEncoder,
                tenantExecutor
        );

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenantId = tenant.getId();
        TenantContext.setTenantId(tenant.getId());

        user = new User(
                "Test",
                "User",
                "test@example.com",
                Set.of(EMPLOYEE),
                new UserCredentials(
                        "test@example.com",
                        "password"
                ),
                tenantId
        );
        user.setId(UUID.randomUUID());
        userId = user.getId();

//        UserCredentials credentials = new UserCredentials(
//                "test@example.com",
//                "hashedPassword",
//                Set.of(EMPLOYEE),
//                tenant
//        );

        userRegistrationRequest = new UserRegistrationRequest(
                "Test",
                "User",
                "test@example.com",
                Set.of(EMPLOYEE),
                new CredentialsRegistrationRequest(
                        "test@example.com",
                        "password",
                        "password"
                )
        );
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        // Arrange
        when(userRepository.findByIdAndUserTenantRoles_TenantId(userId, TenantContext.getTenantId())).thenReturn(Optional.of(user));

        // Act
        UserResponse foundUser = userService.findById(userId);

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.id()).isEqualTo(userId);
        assertThat(foundUser.contactEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByIdAndUserTenantRoles_TenantId(userId, TenantContext.getTenantId());
    }

    @Test
    void findById_shouldThrowException_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByIdAndUserTenantRoles_TenantId(userId, TenantContext.getTenantId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find USER with ID '" + userId + "'.");
        
        verify(userRepository, times(1)).findByIdAndUserTenantRoles_TenantId(userId, TenantContext.getTenantId());
    }

    @Test
    void save_shouldSaveAndReturnUser_whenRequestIsValid() {
        // Arrange
        when(tenantRepository.existsById(tenantId)).thenReturn(true);
        when(userRepository.findByContactEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        // Act
        UserResponse savedUser = userService.save(userRegistrationRequest);

        // Assert
        assertNotNull(savedUser);
        assertEquals(userId, savedUser.id());
        assertEquals("Test", savedUser.firstName());
        assertEquals("User", savedUser.lastName());
        assertEquals("test@example.com", savedUser.contactEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void save_shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
        when(tenantRepository.existsById(tenantId)).thenReturn(true);
        when(userRepository.findByContactEmail(anyString())).thenReturn(Optional.of(user));

        // Act
        DuplicateResourceException thrown = catchThrowableOfType(
                DuplicateResourceException.class,
                ()-> userService.save(userRegistrationRequest)
        );

        // Assert
        assertThat(thrown)
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("An existing USER already exists with the provided arguments.");
        
        verify(userRepository, times(1)).findByContactEmail(anyString());
        verifyNoMoreInteractions(userRepository);

        assertThat(thrown.getFields())
                .containsExactlyInAnyOrder("contactEmail");

        assertThat(thrown.getResourceType())
                .isEqualTo(USER);
    }
}
