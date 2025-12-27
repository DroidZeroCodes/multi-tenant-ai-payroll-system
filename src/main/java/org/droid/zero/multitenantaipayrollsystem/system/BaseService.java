package org.droid.zero.multitenantaipayrollsystem.system;

import lombok.RequiredArgsConstructor;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class BaseService {

    protected Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    protected User getCurrentUser() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    protected void invalidateSession() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
