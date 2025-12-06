package org.droid.zero.multitenantaipayrollsystem.system;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.droid.zero.multitenantaipayrollsystem.tenant.Tenant;
import org.droid.zero.multitenantaipayrollsystem.tenant.TenantRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DBDataInitializer {

    private final TenantRepository tenantRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void devStartup() {
        log.info("✅ Dev application is ready");
        log.info("🚩 Starting Database initialization...");

        Tenant tenant = new Tenant();
        tenant.setName("origin");
        tenant.setEmail("orgin@email.com");
        tenant.setPhone("+63 9123456789");
        tenant.setIndustry("Technology");
        tenantRepository.save(tenant);

        log.info("✅ Database initialization completed");
    }
}
