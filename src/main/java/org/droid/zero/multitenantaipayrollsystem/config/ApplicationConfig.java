package org.droid.zero.multitenantaipayrollsystem.config;

import lombok.extern.slf4j.Slf4j;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.modules.user.repository.UserRepository;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Slf4j
@Configuration
@EnableMethodSecurity
public class ApplicationConfig {

    private final UserRepository userRepository;

    public ApplicationConfig(@Lazy UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            try {
                UUID tenantId = TenantContext.getTenantId();

                log.debug("loading user for authentication");
                log.debug("username: {}", username);
                log.debug("tenantId: {}", tenantId);

                //Find the user using the user service, throw an exception if not found
                User user = userRepository.findByUserCredentials_EmailIgnoreCase_AndUserTenantRoles_TenantId(username, tenantId)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                log.debug("userId: {}", user.getId());

                //Verify that the user is currently active
                if (!user.isEnabled()) throw new UsernameNotFoundException("Account is disabled");

                user.loadActiveRoles();

                return user;
            } catch (Exception e) {
                throw new UsernameNotFoundException("Could not authenticate user", e);
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
