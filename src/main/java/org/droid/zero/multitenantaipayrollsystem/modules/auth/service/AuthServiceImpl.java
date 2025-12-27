package org.droid.zero.multitenantaipayrollsystem.modules.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.AuthTokenDto;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.ChangeEmailRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.ChangePasswordRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.model.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.repository.UserCredentialsRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.security.jwt.TokenService;
import org.droid.zero.multitenantaipayrollsystem.system.BaseService;
import org.droid.zero.multitenantaipayrollsystem.system.exceptions.ObjectNotFoundException;
import org.droid.zero.multitenantaipayrollsystem.system.util.HeaderUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.system.ResourceType.USER;
import static org.droid.zero.multitenantaipayrollsystem.system.util.HeaderUtils.extractJwt;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends BaseService implements AuthService {

    private final UserCredentialsRepository credentialsRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void changeEmail(ChangeEmailRequest request, UUID userId) {
        //Validate that the user exists
        UserCredentials credentials = credentialsRepository.findByUserId(userId)
                .orElseThrow(() -> new ObjectNotFoundException(USER, userId));

        //Verify that the contactEmail is new
        if (request.email().equals(credentials.getEmail())) throw new IllegalArgumentException("new contactEmail must not be the same as old contactEmail");

        //Update the contactEmail
        credentials.changeEmail(request.email());
    }

    @Transactional
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
        if (!passwordEncoder.matches(oldPassword, credentials.getPasswordHash())) throw new IllegalArgumentException("oldPassword and existing password do not match");

        if (passwordEncoder.matches(newPassword, credentials.getPasswordHash())) throw new IllegalArgumentException("new contactEmail must not be the same as old password");

        //Update the password
        credentials.changePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    @Override
    public AuthTokenDto createToken(HttpServletRequest request) {
        // Get the authentication
        Authentication authentication = getAuthentication();

        String tenantId = HeaderUtils.extractTenantId(request);
        if (tenantId == null || tenantId.isBlank()) throw new IllegalArgumentException("tenant is required");

        // Create jwt
        String token = tokenService.generateToken(authentication, tenantId);

        return new AuthTokenDto(token);
    }

    @Transactional
    @Override
    public void invalidateToken(HttpServletRequest request) {
        //Extract the token from the request
        String token = extractJwt(request);

        //If there is no token, then throw an exception
        if (token == null || token.isBlank()) throw new BadCredentialsException("Invalid token.");

        //Get the user principal
        User user = getCurrentUser();

        if (user == null) throw new InsufficientAuthenticationException("User is not authenticated.");

        //Validate the token and blacklist it
        tokenService.blacklistToken(token, user);

        //Invalidate the session
        invalidateSession();
    }
}
