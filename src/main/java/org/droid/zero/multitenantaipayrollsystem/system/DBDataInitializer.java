package org.droid.zero.multitenantaipayrollsystem.system;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.model.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.modules.department.repository.DepartmentRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.position.repository.PositionRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.listener.TenantScopedEntityListener;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.Tenant;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.repository.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.modules.user.repository.UserRepository;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DBDataInitializer {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void devStartup() {
        TenantScopedEntityListener.runAsRootTenant(() -> {
            log.info("✅ Dev application is ready");
            log.info("🚩 Starting Database cleanup...");

            positionRepository.deleteAll();
            departmentRepository.deleteAll();
            userRepository.deleteAll();
            tenantRepository.deleteAll();

            log.info("✅ Database cleanup completed");
            log.info("🚩 Starting Database initialization...");

            log.info("➡️ Seeding Tenant Data...");
            Tenant tenant = new Tenant(
                    "Payroll Company",
                    "payroll.company@email.com",
                    "+63 9123456789",
                    "Technology"
            );
            tenantRepository.save(tenant);
            UUID tenantId = tenant.getId();
            log.info("✅ Successfully Created Tenant Data");

            TenantContext.setTenantId(tenantId);


            log.info("➡️ Seeding User Data...");
            User superAdminUser = new User(
                    "John",
                    "Doe",
                    "jDoe@email.com",
                    Set.of(UserRole.SUPER_ADMIN),
                    new UserCredentials(
                            "jDoe@email.com",
                            passwordEncoder.encode("password")
                    ),
                    tenantId
            );


            User tenantAdminUser = new User(
                    "Sence",
                    "Montana",
                    "sMontana@email.com",
                    Set.of(UserRole.TENANT_ADMIN),
                    new UserCredentials(
                            "sMontana@email.com",
                            passwordEncoder.encode("password")
                    ),
                    tenantId
            );

            User hrUser = new User(
                    "Jude",
                    "Luanne",
                    "jLuanne@email.com",
                    Set.of(UserRole.HR_OFFICER),
                    new UserCredentials(
                            "jLuanne@email.com",
                            passwordEncoder.encode("password")
                    ),
                    tenantId
            );

            User employeeUser = new User(
                    "Amar",
                    "Tariq",
                    "aTariq@email.com",
                    Set.of(UserRole.EMPLOYEE),
                    new UserCredentials(
                            "aTariq@email.com",
                            passwordEncoder.encode("password")
                    ),
                    tenantId
            );

            userRepository.save(superAdminUser);
            userRepository.save(tenantAdminUser);
            userRepository.save(hrUser);
            userRepository.save(employeeUser);

            TenantContext.clear();

            log.info("✅ Successfully Created User Data");

            log.info("✅ Database initialization completed");
        });
    }
}
