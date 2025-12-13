package org.droid.zero.multitenantaipayrollsystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import org.droid.zero.multitenantaipayrollsystem.security.auth.user.credentials.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.tenant.Tenant;
import org.droid.zero.multitenantaipayrollsystem.tenant.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.user.User;
import org.droid.zero.multitenantaipayrollsystem.user.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.droid.zero.multitenantaipayrollsystem.user.UserRole.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Tag("integration")
@ActiveProfiles(value = "test")
@Import(TestcontainersConfiguration.class)
@Transactional
public class BaseIntegrationTest {

    protected static String SUPER_ADMIN_TOKEN;
    protected static String TENANT_ADMIN_TOKEN;
    protected static String EMPLOYEE_TOKEN;   

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    private String BASE_URL;

    @BeforeAll
    static void setupTokens(
            @Autowired MockMvc staticMockMvc,
            @Autowired TenantRepository tenantRepository,
            @Autowired UserRepository userRepository,
            @Autowired PasswordEncoder passwordEncoder,
            @Value("${api.endpoint.base-url}") String baseUrl
    ) throws Exception {
        Tenant tenant = createTenant(tenantRepository);
        User superAdmin = createSuperAdminUser(userRepository, passwordEncoder, tenant);
        User tenantAdmin = createTenantAdminUser(userRepository, passwordEncoder, tenant);
        User employee = createEmployeeUser(userRepository, passwordEncoder, tenant);

        MvcResult superAdminResult = staticMockMvc.perform(post(baseUrl + "/auth/login")
                        .with(httpBasic(superAdmin.getEmail(), "password"))
                )
                .andExpect(status().isCreated())
                .andReturn();

        SUPER_ADMIN_TOKEN = "Bearer " + JsonPath.read(superAdminResult.getResponse().getContentAsString(), "$.data.token");

        MvcResult tenantAdminResult = staticMockMvc.perform(post(baseUrl + "/auth/login")
                        .with(httpBasic(tenantAdmin.getEmail(), "password"))
                )
                .andExpect(status().isCreated())
                .andReturn();

        TENANT_ADMIN_TOKEN = "Bearer " + JsonPath.read(tenantAdminResult.getResponse().getContentAsString(), "$.data.token");

        MvcResult employeeResult = staticMockMvc.perform(post(baseUrl + "/auth/login")
                        .with(httpBasic(employee.getEmail(), "password"))
                )
                .andExpect(status().isCreated())
                .andReturn();

        EMPLOYEE_TOKEN = "Bearer " + JsonPath.read(employeeResult.getResponse().getContentAsString(), "$.data.token");
    }

    private static Tenant createTenant(TenantRepository tenantRepository) {
        Tenant tenant = new Tenant();
        tenant.setName("Default Tenant");
        tenant.setEmail("tenant.default@email.com");
        tenant.setPhone("9999999");
        tenant.setIndustry("Default Industry");
        return tenantRepository.save(tenant);
    }

    private static User createSuperAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder, Tenant tenant) {
        UserCredentials credentials = new UserCredentials();
        credentials.setEmail("superAdmin.default@email.com");
        credentials.setPassword(passwordEncoder.encode("password"));
        credentials.setRole(Set.of(SUPER_ADMIN));
        credentials.setTenant(tenant);

        User user = new User();
        user.setFirstName("Default Super Admin");
        user.setLastName("Default Super Admin");
        user.setEmail("superAdmin.default@email.com");
        user.setUserCredentials(credentials);
        user.setTenant(tenant);

        return userRepository.save(user);
    }

    private static User createTenantAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder, Tenant tenant) {
        UserCredentials credentials = new UserCredentials();
        credentials.setEmail("tenantAdmin.default@email.com");
        credentials.setPassword(passwordEncoder.encode("password"));
        credentials.setRole(Set.of(TENANT_ADMIN, EMPLOYEE));
        credentials.setTenant(tenant);

        User user = new User();
        user.setFirstName("Default Tenant Admin");
        user.setLastName("Default Tenant Admin");
        user.setEmail("tenantAdmin.default@email.com");
        user.setUserCredentials(credentials);
        user.setTenant(tenant);

        return userRepository.save(user);
    }

    private static User createEmployeeUser(UserRepository userRepository, PasswordEncoder passwordEncoder, Tenant tenant) {
        UserCredentials credentials = new UserCredentials();
        credentials.setEmail("employee.default@email.com");
        credentials.setPassword(passwordEncoder.encode("password"));
        credentials.setRole(Set.of(EMPLOYEE));
        credentials.setTenant(tenant);

        User user = new User();
        user.setFirstName("Default Employee");
        user.setLastName("Default Employee");
        user.setEmail("employee.default@email.com");
        user.setUserCredentials(credentials);
        user.setTenant(tenant);

        return userRepository.save(user);
    }

    protected String getBaseUrl() {
        return BASE_URL;
    }
}