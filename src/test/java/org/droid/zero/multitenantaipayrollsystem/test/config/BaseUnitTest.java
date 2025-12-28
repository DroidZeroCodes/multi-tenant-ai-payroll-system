package org.droid.zero.multitenantaipayrollsystem.test.config;

import org.droid.zero.multitenantaipayrollsystem.modules.auth.model.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole.SUPER_ADMIN;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(value = "test")
public class BaseUnitTest {

    @BeforeAll
    public static void beforeAll() {
        setupSecurityContext(SUPER_ADMIN);
    }

    protected static void setupSecurityContext(UserRole ...role) {
        User user = new User(
                "firstName",
                "lastName",
                "contactEmail@contactEmail.com",
                Set.of(role),
                new UserCredentials(),
                UUID.randomUUID()
        );

        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(new UsernamePasswordAuthenticationToken(user,null, user.getAuthorities()));
        SecurityContextHolder.setContext(emptyContext);
    }
}