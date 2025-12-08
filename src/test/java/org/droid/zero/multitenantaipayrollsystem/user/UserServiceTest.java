package org.droid.zero.multitenantaipayrollsystem.user;

import org.droid.zero.multitenantaipayrollsystem.system.exceptions.DuplicateResourceException;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserRequest;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserResponse;
import org.droid.zero.multitenantaipayrollsystem.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.USER;
import static org.droid.zero.multitenantaipayrollsystem.user.UserRole.EMPLOYEE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private UserServiceImpl userService;

    private User user;
    private UserRequest userRequest;
    private final UUID userId = UUID.randomUUID();
    private final UUID tenantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper);

        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");
        user.setFirstName("Test");
        user.setLastName("User");

        userRequest = new UserRequest(
                "Test",
                "User",
                "test@example.com",
                "password",
                Set.of(EMPLOYEE),
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
        assertTrue(foundUser.active());
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

        // Act
        UserResponse savedUser = userService.save(userRequest);

        // Assert
        assertNotNull(savedUser);
        assertEquals(userId, savedUser.id());
        assertEquals("Test", savedUser.firstName());
        assertEquals("User", savedUser.lastName());
        assertEquals("test@example.com", savedUser.email());
        assertEquals(Set.of(EMPLOYEE), savedUser.role());
        assertTrue(savedUser.active());

        verify(userRepository, times(1)).existsByEmailIgnoreCaseAndTenantId(anyString(), any(UUID.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void save_shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmailIgnoreCaseAndTenantId(anyString(), any(UUID.class))).thenReturn(true);

        // Act
        DuplicateResourceException thrown = catchThrowableOfType(
                DuplicateResourceException.class,
                ()-> userService.save(userRequest)
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
