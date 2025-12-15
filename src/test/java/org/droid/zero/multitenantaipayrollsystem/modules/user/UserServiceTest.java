package org.droid.zero.multitenantaipayrollsystem.modules.user;

import org.droid.zero.multitenantaipayrollsystem.modules.user.User;
import org.droid.zero.multitenantaipayrollsystem.modules.user.UserRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.UserServiceImpl;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.CredentialsRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.mapper.UserCredentialsMapper;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.DuplicateResourceException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.Tenant;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserResponse;
import org.droid.zero.multitenantaipayrollsystem.modules.user.mapper.UserMapper;
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
import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.USER;
import static org.droid.zero.multitenantaipayrollsystem.modules.user.UserRole.EMPLOYEE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final UserCredentialsMapper credentialsMapper = Mappers.getMapper(UserCredentialsMapper.class);

    private UserServiceImpl userService;

    private Tenant tenant;
    private User user;
    private UserRegistrationRequest userRegistrationRequest;
    private final UUID userId = UUID.randomUUID();
    private final UUID tenantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, tenantRepository, userMapper, credentialsMapper, passwordEncoder);

        tenant = new Tenant();
        tenant.setId(tenantId);

        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setTenant(tenant);

        UserCredentials credentials = new UserCredentials();
        credentials.setEmail("test@example.com");
        credentials.setPassword("hashedPassword");

        userRegistrationRequest = new UserRegistrationRequest(
                "Test",
                "User",
                new CredentialsRegistrationRequest(
                        "test@example.com",
                        "password",
                        "password",
                        Set.of(EMPLOYEE)
                ),
                tenantId
        );
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserResponse foundUser = userService.findById(userId);

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.id()).isEqualTo(userId);
        assertThat(foundUser.email()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findById_shouldThrowException_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find USER with ID '" + userId + "'.");
        
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void save_shouldSaveAndReturnUser_whenRequestIsValid() {
        // Arrange
        when(userRepository.existsByEmailIgnoreCaseAndTenantId(anyString(), any(UUID.class))).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        // Act
        UserResponse savedUser = userService.save(userRegistrationRequest);

        // Assert
        assertNotNull(savedUser);
        assertEquals(userId, savedUser.id());
        assertEquals("Test", savedUser.firstName());
        assertEquals("User", savedUser.lastName());
        assertEquals("test@example.com", savedUser.email());

        verify(userRepository, times(1)).existsByEmailIgnoreCaseAndTenantId(anyString(), any(UUID.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void save_shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmailIgnoreCaseAndTenantId(anyString(), any(UUID.class))).thenReturn(true);
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

        // Act
        DuplicateResourceException thrown = catchThrowableOfType(
                DuplicateResourceException.class,
                ()-> userService.save(userRegistrationRequest)
        );

        // Assert
        assertThat(thrown)
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("An existing USER already exists with the provided arguments.");
        
        verify(userRepository, times(1)).existsByEmailIgnoreCaseAndTenantId(anyString(), any(UUID.class));
        verifyNoMoreInteractions(userRepository);

        assertThat(thrown.getFields())
                .containsExactlyInAnyOrder("email");

        assertThat(thrown.getResourceType())
                .isEqualTo(USER);
    }
}
