package org.droid.zero.multitenantaipayrollsystem.system;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.droid.zero.multitenantaipayrollsystem.tenant.Tenant;
import org.droid.zero.multitenantaipayrollsystem.tenant.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.user.User;
import org.droid.zero.multitenantaipayrollsystem.user.UserRepository;
import org.droid.zero.multitenantaipayrollsystem.user.UserRole;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DBDataInitializer {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

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
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("jDoe@email.com");
        user.setPassword("password");
        user.setRole(Set.of(UserRole.SUPER_ADMIN, UserRole.EMPLOYEE));
        user.setTenant(tenant);
        userRepository.save(user);
        log.info("✅ Successfully Created User Data");

        log.info("✅ Database initialization completed");
    }
}
