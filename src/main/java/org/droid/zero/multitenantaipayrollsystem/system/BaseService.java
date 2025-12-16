package org.droid.zero.multitenantaipayrollsystem.system;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.UserCredentials;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public abstract class BaseService {
    protected UserCredentials getCurrentUser() {
        return (UserCredentials) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    protected boolean isUserLoggedIn() {
        return SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserCredentials;
    }

    protected static void throwAccessDenied() {
        throw new AccessDeniedException("You do not have access to this resource.");
    }

    protected static void throwAccessDenied(String message) {
        throw new AccessDeniedException(message);
    }
}
