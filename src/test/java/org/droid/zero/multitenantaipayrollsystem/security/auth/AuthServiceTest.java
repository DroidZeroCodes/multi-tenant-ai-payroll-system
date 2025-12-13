package org.droid.zero.multitenantaipayrollsystem.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.droid.zero.multitenantaipayrollsystem.security.auth.dto.AuthTokenDto;
import org.droid.zero.multitenantaipayrollsystem.security.auth.dto.ChangeEmailRequest;
import org.droid.zero.multitenantaipayrollsystem.security.auth.dto.ChangePasswordRequest;
import org.droid.zero.multitenantaipayrollsystem.security.auth.user.credentials.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.security.auth.user.credentials.UserCredentialsRepository;
import org.droid.zero.multitenantaipayrollsystem.security.jwt.TokenService;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserCredentialsRepository credentialsRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    private AuthServiceImpl authService;

    private UserCredentials userCredentials;
    private final UUID userId = UUID.randomUUID();
    private final String email = "test@example.com";
    private final String hashedPassword = "$2a$10$hashedPassword";

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(credentialsRepository, tokenService, passwordEncoder);

        userCredentials = new UserCredentials();
        userCredentials.setId(userId);
        userCredentials.setEmail(email);
        userCredentials.setPassword(hashedPassword);
    }

    @Test
    void findByEmail_shouldReturnUserCredentials_whenEmailExists() {
        // Arrange
        when(credentialsRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.of(userCredentials));

        // Act
        UserCredentials result = authService.findByEmail(email);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        verify(credentialsRepository, times(1)).findByEmailIgnoreCase(email);
    }

    @Test
    void findByEmail_shouldThrowException_whenEmailDoesNotExist() {
        // Arrange
        when(credentialsRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.findByEmail(email))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Could not find " + USER + " with email '" + email +"'.");

        verify(credentialsRepository, times(1)).findByEmailIgnoreCase(email);
    }

    @Test
    void changeEmail_shouldUpdateEmail_whenRequestIsValid() {
        // Arrange
        String newEmail = "new@example.com";
        ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest(newEmail);

        when(credentialsRepository.findByUserId(userId))
                .thenReturn(Optional.of(userCredentials));

        // Act
        authService.changeEmail(changeEmailRequest, userId);

        // Assert
        assertThat(userCredentials.getEmail()).isEqualTo(newEmail);
        verify(credentialsRepository, times(1)).findByUserId(userId);
    }

    @Test
    void changeEmail_shouldThrowException_whenNewEmailIsSameAsOldEmail() {
        // Arrange
        ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest(email);

        when(credentialsRepository.findByUserId(userId))
                .thenReturn(Optional.of(userCredentials));

        // Act & Assert
        assertThatThrownBy(() -> authService.changeEmail(changeEmailRequest, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("new email must not be the same as old email");

        verify(credentialsRepository, times(1)).findByUserId(userId);
    }

    @Test
    void changeEmail_shouldThrowException_whenUserDoesNotExist() {
        // Arrange
        ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest("new@example.com");

        when(credentialsRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.changeEmail(changeEmailRequest, userId))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Could not find " + USER + " with ID '" + userId +"'.");

        verify(credentialsRepository, times(1)).findByUserId(userId);
    }

    @Test
    void changePassword_shouldUpdatePassword_whenRequestIsValid() {
        // Arrange
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                oldPassword, newPassword, newPassword
        );

        when(credentialsRepository.findByUserId(userId))
                .thenReturn(Optional.of(userCredentials));
        when(passwordEncoder.matches(oldPassword, hashedPassword))
                .thenReturn(true);
        when(passwordEncoder.matches(newPassword, hashedPassword))
                .thenReturn(false);
        when(passwordEncoder.encode(newPassword))
                .thenReturn("$2a$10$newHashedPassword");

        // Act
        authService.changePassword(changePasswordRequest, userId);

        // Assert
        verify(passwordEncoder, times(1)).matches(oldPassword, hashedPassword);
        verify(passwordEncoder, times(1)).matches(newPassword, hashedPassword);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(credentialsRepository, times(1)).findByUserId(userId);
    }

    @Test
    void changePassword_shouldThrowException_whenNewPasswordDoesNotMatchConfirmPassword() {
        // Arrange
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "oldPassword123", "newPassword123", "differentPassword"
        );

        // Act & Assert
        assertThatThrownBy(() -> authService.changePassword(changePasswordRequest, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("newPassword and confirmPassword does not match");

        verify(credentialsRepository, never()).findByUserId(any());
    }

    @Test
    void changePassword_shouldThrowException_whenOldPasswordDoesNotMatch() {
        // Arrange
        String oldPassword = "wrongOldPassword";
        String newPassword = "newPassword123";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                oldPassword, newPassword, newPassword
        );

        when(credentialsRepository.findByUserId(userId))
                .thenReturn(Optional.of(userCredentials));
        when(passwordEncoder.matches(oldPassword, hashedPassword))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.changePassword(changePasswordRequest, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("oldPassword and existing password do not match");

        verify(passwordEncoder, times(1)).matches(oldPassword, hashedPassword);
        verify(credentialsRepository, times(1)).findByUserId(userId);
    }

    @Test
    void changePassword_shouldThrowException_whenNewPasswordIsSameAsOldPassword() {
        // Arrange
        String oldPassword = "samePassword";
        String newPassword = "samePassword";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                oldPassword, newPassword, newPassword
        );

        when(credentialsRepository.findByUserId(userId))
                .thenReturn(Optional.of(userCredentials));
        when(passwordEncoder.matches(oldPassword, hashedPassword))
                .thenReturn(true);
        when(passwordEncoder.matches(newPassword, hashedPassword))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.changePassword(changePasswordRequest, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("new email must not be the same as old password");

        verify(passwordEncoder, times(2)).matches("samePassword", hashedPassword); //oldPassword and newPassword have the same value so it is merged
        verify(credentialsRepository, times(1)).findByUserId(userId);
    }

    @Test
    void createToken_shouldReturnAuthTokenDto_withGeneratedToken() {
        // Arrange
        String generatedToken = "generated.jwt.token";
        when(tokenService.generateToken(authentication))
                .thenReturn(generatedToken);

        // Act
        AuthTokenDto result = authService.createToken(authentication);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(generatedToken);
        verify(tokenService, times(1)).generateToken(authentication);
    }

    @Test
    void invalidateToken_shouldBlacklistToken_whenTokenIsValid() {
        // Arrange
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);
        when(authentication.getPrincipal())
                .thenReturn(userCredentials);

        // Act
        authService.invalidateToken(request, authentication);

        // Assert
        verify(tokenService, times(1)).blacklistToken(token, userCredentials);
        verify(authentication, times(1)).setAuthenticated(false);
    }

    @Test
    void invalidateToken_shouldThrowException_whenTokenIsBlank() {
        // Arrange
        when(request.getHeader("Authorization"))
                .thenReturn("Bearer ");

        // Act & Assert
        assertThatThrownBy(() -> authService.invalidateToken(request, authentication))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid token.");

        verify(tokenService, never()).blacklistToken(anyString(), any());
        verify(authentication, never()).setAuthenticated(anyBoolean());
    }

    @Test
    void invalidateToken_shouldThrowException_whenNoAuthorizationHeader() {
        // Arrange
        when(request.getHeader("Authorization"))
                .thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> authService.invalidateToken(request, authentication))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid token.");

        verify(tokenService, never()).blacklistToken(anyString(), any());
        verify(authentication, never()).setAuthenticated(anyBoolean());
    }

    @Test
    void invalidateToken_shouldThrowException_whenAuthorizationHeaderDoesNotStartWithBearer() {
        // Arrange
        when(request.getHeader("Authorization"))
                .thenReturn("Basic token");

        // Act & Assert
        assertThatThrownBy(() -> authService.invalidateToken(request, authentication))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid token.");

        verify(tokenService, never()).blacklistToken(anyString(), any());
        verify(authentication, never()).setAuthenticated(anyBoolean());
    }

//    @Test
//    void extractToken_shouldReturnToken_whenBearerTokenIsPresent() {
//        // Arrange
//        String expectedToken = "jwt.token.here";
//        when(request.getHeader("Authorization"))
//                .thenReturn("Bearer " + expectedToken);
//
//        // Act
//        String result = authService.invalidateToken(request, authentication);
//
//        // Note: extractToken is private, so we test it through invalidateToken
//        // For direct testing, you could use reflection or make the method package-private
//
//        // Assert
//        verify(tokenService, times(1)).blacklistToken(expectedToken, userCredentials);
//    }
//
//    @Test
//    void extractToken_shouldReturnNull_whenAuthorizationHeaderIsNull() {
//        // Arrange
//        when(request.getHeader("Authorization"))
//                .thenReturn(null);
//
//        // Act & Assert
//        assertThatThrownBy(() -> authService.invalidateToken(request, authentication))
//                .isInstanceOf(InsufficientAuthenticationException.class);
//
//        verify(tokenService, never()).blacklistToken(anyString(), any());
//    }
}