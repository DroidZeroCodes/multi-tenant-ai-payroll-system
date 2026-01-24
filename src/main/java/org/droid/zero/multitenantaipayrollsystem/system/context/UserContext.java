package org.droid.zero.multitenantaipayrollsystem.system.context;

import org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static java.util.Arrays.*;

@Component
public class UserContext {

    private static final ThreadLocal<String> EMPLOYEE_ID = new ThreadLocal<>();

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            return (User) auth.getPrincipal();
        }

        return null;
    }

    public static boolean hasRoles(UserRole ...roles) {
        User user = getCurrentUser();
        return user != null && user.getActiveRoles().stream()
                .anyMatch(role -> asList(roles).contains(role));
    }

    public static String getEmployeeId() {
        return EMPLOYEE_ID.get();
    }

    public static void setEmployeeId(String employeeId) {
        EMPLOYEE_ID.set(employeeId);
    }

    public static void clearEmployeeId() {
        EMPLOYEE_ID.remove();
    }
}
