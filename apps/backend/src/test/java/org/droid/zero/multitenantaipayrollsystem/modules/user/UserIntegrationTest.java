package org.droid.zero.multitenantaipayrollsystem.modules.user;

import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpHeaders;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.dto.CredentialsRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.auth.model.UserCredentials;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.Tenant;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.repository.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole;
import org.droid.zero.multitenantaipayrollsystem.modules.user.dto.UserRegistrationRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.test.config.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;
import java.util.UUID;

import static org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole.EMPLOYEE;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for User")
class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    @DisplayName("Check findUserById (GET)")
    void findUserById_Success() throws Exception {
        // Arrange
        User user = new User(
                "John",
                "Doe",
                "test@example.com",
                Set.of(EMPLOYEE),
                new UserCredentials(
                        "test@example.com",
                        "password"
                ),
                TEST_TENANT_ID
        );
        user = createUser(user, TEST_TENANT_ID);

        // Act & Assert
        this.mockMvc.perform(get(BASE_URL + "/users/" + user.getId())
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.contactEmail").value("test@example.com"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check findUserById with not found (GET)")
    void findUserById_NotFound() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        this.mockMvc.perform(get(BASE_URL + "/users/" + nonExistentId)
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Could not find USER with ID '" + nonExistentId + "'."))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].status").value(404))
                .andExpect(jsonPath("$.errors[0].code").value("resource_not_found"))
                .andExpect(jsonPath("$.errors[0].title").value("Resource Not Found"))
                .andExpect(jsonPath("$.errors[0].detail").value("Could not find USER with ID '" + nonExistentId + "'."));
    }

    @Test
    @DisplayName("Check findUserById (GET) - Forbidden for unauthorized Employee")
    void findUserById_Forbidden_Employee() throws Exception {
        // Arrange
        User targetUser = new User(
                "Target",
                "User",
                "target@example.com",
                Set.of(EMPLOYEE),
                new UserCredentials(
                        "target@example.com",
                        "password"
                ),
                TEST_TENANT_ID
        );
        targetUser = createUser(targetUser, TEST_TENANT_ID);

        // Act & Assert
        this.mockMvc.perform(get(BASE_URL + "/users/" + targetUser.getId())
                        .header(HttpHeaders.AUTHORIZATION, EMPLOYEE_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Check addUser with valid input (POST)")
    void testAddUser_Success() throws Exception {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest(
                "Jane",
                "Doe",
                "jane.doe@example.com",
                        Set.of(UserRole.HR_OFFICER),
                new CredentialsRegistrationRequest(
                        "jane.doe@example.com",
                        "SecurePass123!",
                        "SecurePass123!"
                )
        );

        // Act & Assert
        this.mockMvc.perform(post(BASE_URL + "/users")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Create Success"))
                .andExpect(jsonPath("$.data.firstName").value("Jane"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.contactEmail").value("jane.doe@example.com"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check addUser with invalid input (POST)")
    void testAddUser_InvalidInput() throws Exception {
        // Arrange - missing required fields
        UserRegistrationRequest request = new UserRegistrationRequest(
                "",
                "",
                        "",
                        Set.of(),
                new CredentialsRegistrationRequest(
                        "invalidEmailFormat",
                        "",
                        ""
                )
        );

        // Act & Assert
        this.mockMvc.perform(post(BASE_URL + "/users")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see errors for details."))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors[*].detail",
                        containsInAnyOrder(
                                "invalid email format",
                                "contactEmail is required",
                                "firstName is required",
                                "lastName is required",
                                "password is required",
                                "confirmPassword is required",
                                "roles is required"
                        )));
    }

    @Test
    @DisplayName("Check addUser (POST) - Forbidden for Employee")
    void testAddUser_Forbidden_Employee() throws Exception {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest(
                "Hacker",
                "Employee",
                "hacker@example.com",
                Set.of(EMPLOYEE),
                new CredentialsRegistrationRequest(
                        "hacker@example.com",
                        "Pass123!",
                        "Pass123!"
                )
        );

        // Act & Assert
        this.mockMvc.perform(post(BASE_URL + "/users")
                        .header(HttpHeaders.AUTHORIZATION, EMPLOYEE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Check contactEmail uniqueness is enforced per tenant")
    void testEmailUniquenessPerTenant() throws Exception {
        // Arrange - Create first user in tenant 1
        UserRegistrationRequest user1Request = new UserRegistrationRequest(
                "User",
                "One",
                "same@contactEmail.com",
                Set.of(EMPLOYEE),
                new CredentialsRegistrationRequest(
                        "same@contactEmail.com",
                        "Password123!",
                        "Password123!"
                )
        );

        // Act & Assert - First user creation should succeed
        this.mockMvc.perform(post(BASE_URL + "/users")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1Request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Arrange - Try to create another user with same contactEmail in same tenant
        UserRegistrationRequest duplicateEmailRequest = new UserRegistrationRequest(
                "User",
                "Two",
                "same@contactEmail.com",
                Set.of(EMPLOYEE),
                new CredentialsRegistrationRequest(
                        "same@contactEmail.com",
                        "Password123!",
                        "Password123!"
                )
        );

        // Act & Assert - Should fail with duplicate contactEmail error
        this.mockMvc.perform(post(BASE_URL + "/users")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmailRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("An existing USER already exists with the provided arguments."));

        // Arrange - Create a second tenant
        Tenant secondTenant = new Tenant(
                "Second Tenant",
                "second@tenant.com",
                "0987654321",
                "Finance"
        );
        secondTenant = tenantRepository.save(secondTenant);

        // Arrange - Try to create user with same contactEmail but in different tenant
        UserRegistrationRequest differentTenantRequest = new UserRegistrationRequest(
                "User",
                "Three",
                "same@contactEmail.com",
                Set.of(EMPLOYEE),
                new CredentialsRegistrationRequest(
                        "same@contactEmail.com",
                        "Password123!",
                        "Password123!"
                        )
        );

        User secondTenantAdmin = createSuperAdminUser(secondTenant.getId());

        MvcResult result = this.mockMvc.perform(post(BASE_URL + "/auth/login")
                        .with(httpBasic(secondTenantAdmin.getUsername(), "password"))
                        .header("X-Tenant-ID", secondTenant.getId())
                )
                .andExpect(status().isCreated())
                .andReturn();

        String secondTenantAdminToken = "Bearer " + JsonPath.read(result.getResponse().getContentAsString(), "$.data.token");

        // Act & Assert - Should succeed as it's a different tenant
        this.mockMvc.perform(post(BASE_URL + "/users")
                        .header(HttpHeaders.AUTHORIZATION, secondTenantAdminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(differentTenantRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.contactEmail").value("same@contactEmail.com"));
    }
}
