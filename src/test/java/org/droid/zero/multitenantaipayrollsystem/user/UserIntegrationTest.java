package org.droid.zero.multitenantaipayrollsystem.user;

import org.droid.zero.multitenantaipayrollsystem.BaseIntegrationTest;
import org.droid.zero.multitenantaipayrollsystem.tenant.Tenant;
import org.droid.zero.multitenantaipayrollsystem.tenant.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.user.dto.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for User")
class UserIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    private Tenant testTenant;

    @BeforeEach
    void setup() {
        // Create a test tenant
        testTenant = new Tenant();
        testTenant.setName("Test Tenant");
        testTenant.setEmail("test@tenant.com");
        testTenant.setPhone("1234567890");
        testTenant.setIndustry("Technology");
        testTenant = tenantRepository.save(testTenant);
    }

    @Test
    @DisplayName("Check findUserById (GET)")
    void findUserById_Success() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("Password123!");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Set.of(UserRole.EMPLOYEE));
        user.setTenant(testTenant);
        user = userRepository.save(user);

        // Act & Assert
        this.mockMvc.perform(get(this.getBaseUrl() + "/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.role.length()").value(1))
                .andExpect(jsonPath("$.data.role[0]").value("EMPLOYEE"))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check findUserById with not found (GET)")
    void findUserById_NotFound() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        this.mockMvc.perform(get(this.getBaseUrl() + "/users/" + nonExistentId)
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
    @DisplayName("Check addUser with valid input (POST)")
    void testAddUser_Success() throws Exception {
        // Arrange
        UserRequest request = new UserRequest(
                "Jane",
                "Doe",
                "jane.doe@example.com",
                "SecurePass123!",
                Set.of(UserRole.HR_OFFICER),
                testTenant.getId()
        );

        // Act & Assert
        this.mockMvc.perform(post(this.getBaseUrl() + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Create Success"))
                .andExpect(jsonPath("$.data.firstName").value("Jane"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.email").value("jane.doe@example.com"))
                .andExpect(jsonPath("$.data.role.length()").value(1))
                .andExpect(jsonPath("$.data.role[0]").value("HR_OFFICER"))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check addUser with invalid input (POST)")
    void testAddUser_InvalidInput() throws Exception {
        // Arrange - missing required fields
        UserRequest request = new UserRequest(
                "",
                "",
                "invalidEmailFormat",
                "",
                Set.of(),
                null
        );

        // Act & Assert
        this.mockMvc.perform(post(this.getBaseUrl() + "/users")
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
                                "firstName is required",
                                "lastName is required",
                                "password is required",
                                "role is required",
                                "tenantId is required"
                        )));
    }

    @Test
    @DisplayName("Check email uniqueness is enforced per tenant")
    void testEmailUniquenessPerTenant() throws Exception {
        // Arrange - Create first user in tenant 1
        UserRequest user1Request = new UserRequest(
                "User",
                "One",
                "same@email.com",
                "Password123!",
                Set.of(UserRole.EMPLOYEE),
                testTenant.getId()
        );

        // Act & Assert - First user creation should succeed
        this.mockMvc.perform(post(this.getBaseUrl() + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1Request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Arrange - Try to create another user with same email in same tenant
        UserRequest duplicateEmailRequest = new UserRequest(
                "User",
                "Two",
                "same@email.com",
                "Password123!",
                Set.of(UserRole.EMPLOYEE),
                testTenant.getId()
        );

        // Act & Assert - Should fail with duplicate email error
        this.mockMvc.perform(post(this.getBaseUrl() + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmailRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("An existing USER already exists with the provided arguments."));

        // Arrange - Create a second tenant
        Tenant secondTenant = new Tenant();
        secondTenant.setName("Second Tenant");
        secondTenant.setEmail("second@tenant.com");
        secondTenant.setPhone("0987654321");
        secondTenant.setIndustry("Finance");
        secondTenant = tenantRepository.save(secondTenant);

        // Arrange - Try to create user with same email but in different tenant
        UserRequest differentTenantRequest = new UserRequest(
                "User",
                "Three",
                "same@email.com",
                "Password123!",
                Set.of(UserRole.EMPLOYEE),
                secondTenant.getId()
        );

        // Act & Assert - Should succeed as it's a different tenant
        this.mockMvc.perform(post(this.getBaseUrl() + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(differentTenantRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("same@email.com"));
    }
}
