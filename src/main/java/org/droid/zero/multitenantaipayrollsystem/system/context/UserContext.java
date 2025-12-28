package org.droid.zero.multitenantaipayrollsystem.system.context;

import org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserContext {
    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            return (User) auth.getPrincipal();
        }

        return null;
    }

    public static boolean hasRole(UserRole role) {
        User user = getCurrentUser();
        return user != null && user.getActiveRoles().stream()
                .anyMatch(a -> a.equals(role));
    }
}
