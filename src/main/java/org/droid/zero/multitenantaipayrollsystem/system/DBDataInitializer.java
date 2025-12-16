package org.droid.zero.multitenantaipayrollsystem.system;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.Tenant;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.repository.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.User;
import org.droid.zero.multitenantaipayrollsystem.modules.user.UserRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.UserRole;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DBDataInitializer {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void devStartup() {
        log.info("✅ Dev application is ready");
        log.info("🚩 Starting Database initialization...");

        log.info("➡️ Seeding Tenant Data...");
        Tenant tenant = new Tenant();
        tenant.setName("origin");
        tenant.setEmail("orgin@email.com");
        tenant.setPhone("+63 9123456789");
        tenant.setIndustry("Technology");
        tenantRepository.save(tenant);
        log.info("✅ Successfully Created Tenant Data");

        log.info("➡️ Seeding User Data...");
        User superAdminUser = new User();
        superAdminUser.setFirstName("John");
        superAdminUser.setLastName("Doe");
        superAdminUser.setEmail("jDoe@email.com");
        superAdminUser.setTenant(tenant);

        UserCredentials superAdminCredentials = new UserCredentials();
        superAdminCredentials.setEmail("jDoe@email.com");
        superAdminCredentials.setPassword(passwordEncoder.encode("password"));
        superAdminCredentials.setRole(Set.of(UserRole.SUPER_ADMIN, UserRole.EMPLOYEE));
        superAdminCredentials.setTenant(tenant);

        superAdminUser.setUserCredentials(superAdminCredentials);

        User tenantAdminUser = new User();
        tenantAdminUser.setFirstName("Sence");
        tenantAdminUser.setLastName("Montana");
        tenantAdminUser.setEmail("sMontana@email.com");
        tenantAdminUser.setTenant(tenant);

        UserCredentials tenantAdminCredentials = new UserCredentials();
        tenantAdminCredentials.setEmail("sMontana@email.com");
        tenantAdminCredentials.setPassword(passwordEncoder.encode("password"));
        tenantAdminCredentials.setRole(Set.of(UserRole.TENANT_ADMIN));
        tenantAdminCredentials.setTenant(tenant);

        tenantAdminUser.setUserCredentials(tenantAdminCredentials);

        User employeeUser = new User();
        employeeUser.setFirstName("Amar");
        employeeUser.setLastName("Tariq");
        employeeUser.setEmail("aTariq@email.com");
        employeeUser.setTenant(tenant);

        UserCredentials employeeCredentials = new UserCredentials();
        employeeCredentials.setEmail("aTariq@email.com");
        employeeCredentials.setPassword(passwordEncoder.encode("password"));
        employeeCredentials.setRole(Set.of(UserRole.TENANT_ADMIN));
        employeeCredentials.setTenant(tenant);

        employeeUser.setUserCredentials(employeeCredentials);

        userRepository.save(superAdminUser);
        userRepository.save(tenantAdminUser);
        userRepository.save(employeeUser);

        log.info("✅ Successfully Created User Data");

        log.info("✅ Database initialization completed");
    }
}
