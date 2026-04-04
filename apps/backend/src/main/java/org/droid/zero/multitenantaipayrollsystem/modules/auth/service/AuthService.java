package org.droid.zero.multitenantaipayrollsystem.modules.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.AuthTokenDto;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.ChangeEmailRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.ChangePasswordRequest;

import java.util.UUID;

public interface AuthService {

    void changeEmail(ChangeEmailRequest request, UUID userId);

    void changePassword(ChangePasswordRequest request, UUID userId);

    AuthTokenDto createToken(HttpServletRequest request);

    void invalidateToken(HttpServletRequest request);
}
