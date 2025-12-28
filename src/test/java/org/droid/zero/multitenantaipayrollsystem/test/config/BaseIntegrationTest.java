package org.droid.zero.multitenantaipayrollsystem.test.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.model.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.listener.TenantScopedEntityListener;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.Tenant;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.repository.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.modules.user.repository.UserRepository;
import org.droid.zero.multitenantaipayrollsystem.security.filters.RateLimitCheckFilter;
import org.droid.zero.multitenantaipayrollsystem.system.TenantExecutor;
import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;
import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Tag("integration")
@ActiveProfiles(value = "test")
@Import(TestcontainersConfiguration.class)
public class BaseIntegrationTest {

    public static String BASE_URL;
    public static String SUPER_ADMIN_TOKEN;
    public static String TENANT_ADMIN_TOKEN;
    public static String EMPLOYEE_TOKEN;
    public static UUID TEST_TENANT_ID;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TenantExecutor tenantExecutor;

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @BeforeEach
    protected void setupTokens(
            @Autowired MockMvc staticMockMvc,
            @Autowired TenantRepository tenantRepository,
            @Autowired UserRepository userRepository,
            @Autowired PasswordEncoder passwordEncoder,
            @Value("${api.endpoint.base-url}") String baseUrl
    ) {
        RateLimitCheckFilter.disable();

        this.mockMvc = staticMockMvc;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;

        TenantScopedEntityListener.runWithoutTenantChecks(() -> {
            userRepository.deleteAll();
            tenantRepository.deleteAll();
            Tenant tenant = new Tenant(
                    "Default Tenant",
                    "tenant.default@email.com",
                    "9999999",
                    "Default Industry"
            );
            tenantRepository.save(tenant);
            UUID tenantId = tenant.getId();

            TEST_TENANT_ID = tenant.getId();
            TenantContext.setTenantId(tenantId);

            User superAdmin = createSuperAdminUser(tenantId);
            User tenantAdmin = createTenantAdminUser(tenantId);
            User employee = createEmployeeUser(tenantId);

            BASE_URL = baseUrl;

            try {
                SUPER_ADMIN_TOKEN = getToken(tenantId, superAdmin.getContactEmail());
                TENANT_ADMIN_TOKEN = getToken(tenantId, tenantAdmin.getContactEmail());
                EMPLOYEE_TOKEN = getToken(tenantId, employee.getContactEmail());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        RateLimitCheckFilter.enable();
        TenantContext.clear();
    }

    protected String getToken(UUID tenantId, String email) throws Exception {
        SecurityContextHolder.clearContext();
        TestSecurityContextHolder.clearContext();

        MvcResult result = this.mockMvc.perform(post(BASE_URL + "/auth/login")
                        .with(httpBasic(email, "password"))
                        .header("X-Tenant-ID", tenantId)
                )
                .andExpect(status().isCreated())
                .andReturn();

        return "Bearer " + JsonPath.read(result.getResponse().getContentAsString(), "$.data.token");
    }

    protected User createSuperAdminUser(UUID tenantId) {
        TenantContext.setTenantId(tenantId);
        UserCredentials credentials = new UserCredentials(
                "superAdmin.default@email.com",
                this.passwordEncoder.encode("password")
        );

        User user = new User(
                "Default Super Admin",
                "Default Super Admin",
                "superAdmin.default@email.com",
                Set.of(SUPER_ADMIN),
                credentials,
                tenantId
        );


        User saved = userRepository.save(user);
        TenantContext.setTenantId(TEST_TENANT_ID);

        return saved;
    }

    protected User createTenantAdminUser(UUID tenantId) {
        TenantContext.setTenantId(tenantId);
        UserCredentials credentials = new UserCredentials(
                "tenantAdmin.default@email.com",
                this.passwordEncoder.encode("password")
        );

        User user = new User(
                "Default Tenant Admin",
                "Default Tenant Admin",
                "tenantAdmin.default@email.com",
                Set.of(TENANT_ADMIN),
                credentials,
                tenantId
        );

        User saved = userRepository.save(user);
        TenantContext.setTenantId(TEST_TENANT_ID);

        return saved;
    }

    protected User createEmployeeUser(UUID tenantId) {
        TenantContext.setTenantId(tenantId);
        UserCredentials credentials = new UserCredentials(
                "employee.default@email.com",
                this.passwordEncoder.encode("password")
        );

        User user = new User(
                "Default Employee",
                "Default Employee",
                "employee.default@email.com",
                Set.of(EMPLOYEE),
                credentials,
                tenantId
        );

        User saved = userRepository.save(user);
        TenantContext.setTenantId(TEST_TENANT_ID);

        return saved;
    }

    protected User createUser(User user, UUID tenantId) {
        TenantContext.setTenantId(tenantId);
        User saved = userRepository.save(user);
        TenantContext.setTenantId(TEST_TENANT_ID);

        return saved;
    }
}