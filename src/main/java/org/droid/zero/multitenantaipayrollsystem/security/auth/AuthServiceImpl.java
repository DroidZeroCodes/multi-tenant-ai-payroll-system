package org.droid.zero.multitenantaipayrollsystem.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.security.auth.dto.AuthTokenDto;
import org.droid.zero.multitenantaipayrollsystem.security.auth.dto.ChangeEmailRequest;
import org.droid.zero.multitenantaipayrollsystem.security.auth.dto.ChangePasswordRequest;
import org.droid.zero.multitenantaipayrollsystem.security.auth.user.credentials.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.security.auth.user.credentials.UserCredentialsRepository;
import org.droid.zero.multitenantaipayrollsystem.security.jwt.TokenService;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.USER;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserCredentialsRepository credentialsRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserCredentials findByEmail(String email) {
        return credentialsRepository.findByEmailIgnoreCase(email)
                .orElseThrow(()-> new ObjectNotFoundException(USER, email, "email"));
    }

    @Override
    public void changeEmail(ChangeEmailRequest request, UUID userId) {
        //Validate that the user exists
        UserCredentials credentials = credentialsRepository.findByUserId(userId)
                .orElseThrow(() -> new ObjectNotFoundException(USER, userId));

        //Verify that the email is new
        if (request.email().equals(credentials.getEmail())) throw new IllegalArgumentException("new email must not be the same as old email");

        //Update the email
        credentials.setEmail(request.email());
    }

    @Override
    public void changePassword(ChangePasswordRequest request, UUID userId) {
        //Verify that the new password and confirm password matches
        String newPassword = request.newPassword();
        if (!newPassword.equals(request.confirmPassword())) throw new IllegalArgumentException("newPassword and confirmPassword does not match");

        //Validate that the user exists
        UserCredentials credentials = credentialsRepository.findByUserId(userId)
                .orElseThrow(() -> new ObjectNotFoundException(USER, "userId"));

        //Verify that the oldPassword in the request matches the current password
        String oldPassword = request.oldPassword();
        if (!passwordEncoder.matches(oldPassword, credentials.getPassword())) throw new IllegalArgumentException("oldPassword and existing password do not match");

        if (passwordEncoder.matches(newPassword, credentials.getPassword())) throw new IllegalArgumentException("new email must not be the same as old password");

        //Update the password
        credentials.setPassword(passwordEncoder.encode(newPassword));
    }

    @Override
    public AuthTokenDto createToken(Authentication authentication) {
        // Create jwt
        String token = tokenService.generateToken(authentication);

        return new AuthTokenDto(token);
    }

    @Override
    public void invalidateToken(HttpServletRequest request, Authentication authentication) {
        //Verify that the user has a valid session
        if (authentication == null) throw new InsufficientAuthenticationException("User is not authenticated");

        //Extract the token from the request
        String token = extractToken(request);

        //If there is no token, then throw an exception
        if (token == null || token.isBlank()) throw new BadCredentialsException("Invalid token.");

        //Get the user principal
        UserCredentials user = (UserCredentials) authentication.getPrincipal();

        //Validate the token and blacklist it
        tokenService.blacklistToken(token, user);

        //Invalidate the session
        authentication.setAuthenticated(false);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        return token;
    }
}
