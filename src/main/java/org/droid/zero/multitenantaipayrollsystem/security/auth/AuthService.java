package org.droid.zero.multitenantaipayrollsystem.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.droid.zero.multitenantaipayrollsystem.security.auth.dto.AuthTokenDto;
import org.droid.zero.multitenantaipayrollsystem.security.auth.dto.ChangeEmailRequest;
import org.droid.zero.multitenantaipayrollsystem.security.auth.dto.ChangePasswordRequest;
import org.droid.zero.multitenantaipayrollsystem.security.auth.user.credentials.UserCredentials;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface AuthService {
    UserCredentials findByEmail(String email);
    void changeEmail(ChangeEmailRequest request, UUID userId);
    void changePassword(ChangePasswordRequest request, UUID userId);

    AuthTokenDto createToken(Authentication authentication);
    void invalidateToken(HttpServletRequest request, Authentication authentication);


}
