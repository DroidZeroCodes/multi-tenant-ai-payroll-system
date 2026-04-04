package org.droid.zero.multitenantaipayrollsystem.modules.tenant;

import org.apache.http.HttpHeaders;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.dto.TenantRequest;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.model.Tenant;
import org.droid.zero.multitenantaipayrollsystem.modules.tenant.repository.TenantRepository;
import org.droid.zero.multitenantaipayrollsystem.modules.user.model.User;
import org.droid.zero.multitenantaipayrollsystem.modules.user.repository.UserRepository;
import org.droid.zero.multitenantaipayrollsystem.test.config.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.droid.zero.multitenantaipayrollsystem.modules.user.constant.UserRole.TENANT_ADMIN;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for Tenant")
class TenantIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Check findTenantById (GET) - Success for Super Admin")
    void findTenantById_Success_SuperAdmin() throws Exception {
        //Act & Assert
        this.mockMvc.perform(get(BASE_URL+ "/tenants/" + TEST_TENANT_ID)
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.name").value("Default Tenant"))
                .andExpect(jsonPath("$.data.email").value("tenant.default@email.com"))
                .andExpect(jsonPath("$.data.phone").value("9999999"))
                .andExpect(jsonPath("$.data.industry").value("Default Industry"))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check findTenantById (GET) - Success for Tenant Admin on assigned tenant")
    void findTenantById_Success_TenantAdmin() throws Exception {
        //Act & Assert
        this.mockMvc.perform(get(BASE_URL+ "/tenants/" + TEST_TENANT_ID)
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.name").value("Default Tenant"))
                .andExpect(jsonPath("$.data.email").value("tenant.default@email.com"))
                .andExpect(jsonPath("$.data.phone").value("9999999"))
                .andExpect(jsonPath("$.data.industry").value("Default Industry"))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.errors").isEmpty());
    }


    @Test
    @DisplayName("Check findTenantById with not found (GET)")
    void findTenantById_NotFound() throws Exception {
        //Arrange
        UUID nonExistentId = UUID.randomUUID();

        //Act & Assert
        this.mockMvc.perform(get(BASE_URL+ "/tenants/" + nonExistentId)
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Could not find TENANT with ID '" + nonExistentId + "'."))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].status").value(404))
                .andExpect(jsonPath("$.errors[0].code").value("resource_not_found"))
                .andExpect(jsonPath("$.errors[0].title").value("Resource Not Found"))
                .andExpect(jsonPath("$.errors[0].detail").value("Could not find TENANT with ID '" + nonExistentId + "'."));
    }

    @Test
    @DisplayName("Check findTenantById (GET) - Forbidden for Tenant Admin accessing other tenant")
    void findTenantById_Forbidden_TenantAdmin_CrossTenant() throws Exception {
        Tenant tenant = new Tenant(
                "My Tenant",
                "me@email.com",
                "1112223333",
                "tech"
        );
        tenant = tenantRepository.save(tenant);

        //This tenant admin is not assigned to the new tenant
        this.mockMvc.perform(get(BASE_URL+ "/tenants/" + tenant.getId())
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Check findTenantById (GET) - Forbidden for Employee")
    void findTenantById_Forbidden_Employee() throws Exception {
        //Act & Assert
        this.mockMvc.perform(get(BASE_URL+ "/tenants/" + TEST_TENANT_ID)
                        .header(HttpHeaders.AUTHORIZATION, EMPLOYEE_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Check addTenant with valid input (POST)")
    void addTenant_Success() throws Exception {
        //Arrange
        TenantRequest request = new TenantRequest(
                "Test Company",
                "testEmail@email.com",
                "1234567890",
                "technology"
        );

        //Act & Assert
        this.mockMvc.perform(post(BASE_URL+ "/tenants")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Create Success"))
                .andExpect(jsonPath("$.data.name").value("Test Company"))
                .andExpect(jsonPath("$.data.email").value("testEmail@email.com"))
                .andExpect(jsonPath("$.data.phone").value("1234567890"))
                .andExpect(jsonPath("$.data.industry").value("technology"))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.errors").isEmpty());

        // Verify the tenant was created in the database
        assertThat(tenantRepository.count()).isEqualTo(2); //Including the default tenant

        // Find the newly created tenant
        var newTenant = tenantRepository.findByEmailIgnoreCase("testEmail@email.com").orElseThrow();

        // Verify the default admin user was created for the tenant
        assertThat(userRepository.count()).isEqualTo(4); // Including the default users

        // Find the admin user for this tenant
        var adminUsers = tenantExecutor.executeAsTenant(newTenant.getId(),
                () -> userRepository.findAllByTenantAndRoles(newTenant.getId(), Set.of(TENANT_ADMIN)));

        assertThat(adminUsers).hasSize(1);

        Optional<User> adminUser = adminUsers.stream().findFirst();
        assertThat(adminUser.get().getContactEmail()).isEqualTo("testEmail@email.com");
        assertThat(adminUser.get().getFirstName()).isEqualTo("Test Company");
        assertThat(adminUser.get().getLastName()).isEqualTo("Admin");
        assertThat(adminUser.get().getTenantIds()).contains(newTenant.getId());
    }

    // Add this new test to verify default admin user creation
    @Test
    @DisplayName("Check that creating tenant initializes default admin user")
    void createTenant_InitializesDefaultAdmin() throws Exception {
        // Arrange
        TenantRequest request = new TenantRequest(
                "New Company Inc",
                "company@example.com",
                "0987654321",
                "Finance"
        );

        // Get initial counts
        long initialTenantCount = tenantRepository.count();
        long initialUserCount = userRepository.count();

        // Act
        String response = this.mockMvc.perform(post(BASE_URL + "/tenants")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.active").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract tenant ID from response
        String tenantId = objectMapper.readTree(response)
                .path("data")
                .path("id")
                .asText();

        // Assert - Verify tenant was created
        assertThat(tenantRepository.count()).isEqualTo(initialTenantCount + 1);

        // Find the created tenant
        var createdTenant = tenantRepository.findById(UUID.fromString(tenantId)).orElseThrow();
        assertThat(createdTenant.getName()).isEqualTo("New Company Inc");
        assertThat(createdTenant.getEmail()).isEqualTo("company@example.com");

        // Assert - Verify default admin user was created
        assertThat(userRepository.count()).isEqualTo(initialUserCount + 1);

        // Find and verify the admin user
        var adminUsers = tenantExecutor.executeAsTenant(
                createdTenant.getId(),
                () -> userRepository.findAllByTenantAndRoles(
                        createdTenant.getId(),
                        Set.of(TENANT_ADMIN))
        );
        assertThat(adminUsers).hasSize(1);

        Optional<User> adminUser = adminUsers.stream().findFirst();
        assertThat(adminUser.get().getContactEmail()).isEqualTo("company@example.com");
        assertThat(adminUser.get().getTenantIds()).contains(createdTenant.getId());
        assertThat(adminUser.get().isEnabled()).isTrue();

        // Verify the user has proper admin attributes
        assertThat(adminUser.get().getFirstName()).isEqualTo("New Company Inc");
        assertThat(adminUser.get().getLastName()).isEqualTo("Admin");
        assertThat(adminUser.get().getUsername()).isEqualTo("company@example.com");
        assertThat(adminUser.get().getPassword()).isNotBlank(); // Password should be set
    }

    @Test
    @DisplayName("Check addTenant with invalid input (POST)")
    void addTenant_InvalidInput() throws Exception {
        //Arrange
        TenantRequest request = new TenantRequest(
                "",
                "invalidEmail",
                "1234567890",
                "technology"
        );

        //Act & Assert
        this.mockMvc.perform(post(BASE_URL+ "/tenants")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see errors for details."))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.errors[0].status").value(400))
                .andExpect(jsonPath("$.errors[0].code").value("invalid_format"))
                .andExpect(jsonPath("$.errors[0].title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors[*].detail",
                        containsInAnyOrder("invalid email format", "name is required")));
    }

    @Test
    @DisplayName("Check addTenant with unique constrain violation (POST)")
    void addTenant_UniqueConstraintViolation() throws Exception {
        //Arrange
        Tenant existingTenant = new Tenant(
                "existingTenantName",
                "existing@email.com",
                "11111111111",
                "technology"
        );
        tenantRepository.save(existingTenant);

        TenantRequest request = new TenantRequest(
                "existingTenantName",
                "existing@email.com",
                "11111111111",
                "technology"
        );

        //Act & Assert
        this.mockMvc.perform(post(BASE_URL+ "/tenants")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("An existing TENANT already exists with the provided arguments."))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors.length()").value(3))
                .andExpect(jsonPath("$.errors[0].status").value(409))
                .andExpect(jsonPath("$.errors[0].code").value("duplicate_value"))
                .andExpect(jsonPath("$.errors[0].title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors[0].detail").value("The provided 'name' is already taken."))
                .andExpect(jsonPath("$.errors[1].detail").value("The provided 'contactEmail' is already taken."))
                .andExpect(jsonPath("$.errors[2].detail").value("The provided 'phone' is already taken."));
    }

    @Test
    @DisplayName("Check addTenant (POST) - Forbidden for non Super Admin")
    void addTenant_Forbidden_NonSuperAdmin() throws Exception {
        TenantRequest request = new TenantRequest(
                "Hacker Corp", "hacker@email.com", "0000000000", "crime"
        );

        this.mockMvc.perform(post(BASE_URL+ "/tenants")
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        this.mockMvc.perform(post(BASE_URL+ "/tenants")
                        .header(HttpHeaders.AUTHORIZATION, EMPLOYEE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("Check updateTenant with valid input (PUT) - Success for SuperAdmin")
    void updateTenant_Success_SuperAdmin() throws Exception {
        //Arrange
        Tenant existingTenant = new Tenant(
                "existingTenantName",
                "existing@email.com",
                "11111111111",
                "technology"
        );
        tenantRepository.save(existingTenant);

        TenantRequest request = new TenantRequest(
                "newName",
                "new@email.com",
                "9876543210",
                "technology"
        );

        //Act & Assert
        this.mockMvc.perform(put(BASE_URL+ "/tenants/" + existingTenant.getId())
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.name").value("newName"))
                .andExpect(jsonPath("$.data.email").value("new@email.com"))
                .andExpect(jsonPath("$.data.phone").value("9876543210"))
                .andExpect(jsonPath("$.data.industry").value("technology"))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.errors").isEmpty());

        // Verify tenant was updated in database
        Tenant updatedTenant = tenantRepository.findById(existingTenant.getId()).orElseThrow();
        assertThat(updatedTenant.getName()).isEqualTo("newName");
        assertThat(updatedTenant.getEmail()).isEqualTo("new@email.com");
        assertThat(updatedTenant.getPhone()).isEqualTo("9876543210");
    }

    @Test
    @DisplayName("Check updateTenant with valid input (PUT) - Success for Tenant Admin on assigned tenant")
    void updateTenant_Success_TenantAdmin() throws Exception {
        //Arrange
        TenantRequest request = new TenantRequest(
                "newName",
                "new@email.com",
                "9876543210",
                "technology"
        );

        //Act & Assert
        this.mockMvc.perform(put(BASE_URL+ "/tenants/" + TEST_TENANT_ID)
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.name").value("newName"))
                .andExpect(jsonPath("$.data.email").value("new@email.com"))
                .andExpect(jsonPath("$.data.phone").value("9876543210"))
                .andExpect(jsonPath("$.data.industry").value("technology"))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.errors").isEmpty());

        // Verify tenant was updated in database
        Tenant updatedTenant = tenantRepository.findById(TEST_TENANT_ID).orElseThrow();
        assertThat(updatedTenant.getName()).isEqualTo("newName");
        assertThat(updatedTenant.getEmail()).isEqualTo("new@email.com");
        assertThat(updatedTenant.getPhone()).isEqualTo("9876543210");
    }

    @Test
    @DisplayName("Check updateTenant with not found (PUT)")
    void updateTenant_NotFound() throws Exception {
        //Arrange
        UUID nonExistentId = UUID.randomUUID();
        TenantRequest request = new TenantRequest(
                "newName",
                "new@email.com",
                "9876543210",
                "technology"
        );

        //Act & Assert
        this.mockMvc.perform(put(BASE_URL+ "/tenants/" + nonExistentId)
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Could not find TENANT with ID '" + nonExistentId + "'."))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].status").value(404))
                .andExpect(jsonPath("$.errors[0].code").value("resource_not_found"))
                .andExpect(jsonPath("$.errors[0].title").value("Resource Not Found"))
                .andExpect(jsonPath("$.errors[0].detail").value("Could not find TENANT with ID '" + nonExistentId + "'."));
    }

    @Test
    @DisplayName("Check updateTenant with unique constrain violation (PUT)")
    void updateTenant_UniqueConstraintViolation() throws Exception {
        //Arrange
        Tenant existingTenant = new Tenant(
                "existingTenantName",
                "existing@email.com",
                "11111111111",
                "technology"
        );
        tenantRepository.save(existingTenant);

        Tenant existingAnotherTenant = new Tenant(
                "anotherTenantName",
                "another@email.com",
                "9876543210",
                "technology"
        );
        tenantRepository.save(existingAnotherTenant);

        TenantRequest request = new TenantRequest(
                "anotherTenantName",
                "another@email.com",
                "9876543210",
                "technology"
        );

        //Act & Assert
        this.mockMvc.perform(put(BASE_URL+ "/tenants/" + existingTenant.getId())
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("An existing TENANT already exists with the provided arguments."))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors.length()").value(3))
                .andExpect(jsonPath("$.errors[0].status").value(409))
                .andExpect(jsonPath("$.errors[0].code").value("duplicate_value"))
                .andExpect(jsonPath("$.errors[0].title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors[0].detail").value("The provided 'name' is already taken."))
                .andExpect(jsonPath("$.errors[1].detail").value("The provided 'email' is already taken."))
                .andExpect(jsonPath("$.errors[2].detail").value("The provided 'phone' is already taken."));
    }

    @Test
    @DisplayName("Check updateTenant (PUT) - Forbidden for Tenant Admin on Different Tenant")
    void updateTenant_Forbidden_TenantAdmin_CrossTenant() throws Exception {
        Tenant tenant = new Tenant(
                "Other Tenant",
                "other@email.com",
                "9998887777",
                "other"
        );
        tenant = tenantRepository.save(tenant);

        TenantRequest request = new TenantRequest(
                "Malicious Update", "other@email.com", "9998887777", "other"
        );

        // Act & Assert - Tenant is not assigned to the new tenant
        this.mockMvc.perform(put(BASE_URL+ "/tenants/" + tenant.getId())
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Check updateTenant (PUT) - Forbidden for Employee")
    void updateTenant_Forbidden_Employee() throws Exception {
        Tenant tenant = new Tenant(
                "Other Tenant",
                "other@email.com",
                "9998887777",
                "other"
        );
        tenant = tenantRepository.save(tenant);

        TenantRequest request = new TenantRequest(
                "Malicious Update", "other@email.com", "9998887777", "other"
        );

        // Act & Assert - Tenant is not assigned to the new tenant
        this.mockMvc.perform(put(BASE_URL+ "/tenants/" + tenant.getId())
                        .header(HttpHeaders.AUTHORIZATION, EMPLOYEE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("Check updateTenantStatus with valid input (PATCH)")
    void updateTenantStatus_Success() throws Exception {
        //Arrange
        Tenant existingTenant = new Tenant(
                "existingTenantName",
                "existing@email.com",
                "11111111111",
                "technology"
        );
        tenantRepository.save(existingTenant);

        //Act & Assert - First toggle should deactivate
        this.mockMvc.perform(patch(BASE_URL+ "/tenants/" + existingTenant.getId() + "/status")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.errors").isEmpty());

        // Verify status was updated in the database
        Tenant updatedTenant = tenantRepository.findById(existingTenant.getId()).orElseThrow();
        assertThat(updatedTenant.isActive()).isFalse();
    }

    @Test
    @DisplayName("Check updateTenantStatus toggle multiple times (PATCH)")
    void updateTenantStatus_ToggleMultipleTimes() throws Exception {
        //Arrange
        Tenant existingTenant = new Tenant(
                "existingTenantName",
                "existing@email.com",
                "11111111111",
                "technology"
        );
        tenantRepository.save(existingTenant);

        // First toggle - deactivate
        this.mockMvc.perform(patch(BASE_URL+ "/tenants/" + existingTenant.getId() + "/status")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Second toggle - reactivate
        this.mockMvc.perform(patch(BASE_URL+ "/tenants/" + existingTenant.getId() + "/status")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check updateTenantStatus with not found (PATCH)")
    void updateTenantStatus_NotFound() throws Exception {
        //Arrange
        UUID nonExistentId = UUID.randomUUID();

        //Act & Assert
        this.mockMvc.perform(patch(BASE_URL+ "/tenants/" + nonExistentId + "/status")
                        .header(HttpHeaders.AUTHORIZATION, SUPER_ADMIN_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Could not find TENANT with ID '" + nonExistentId + "'."))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors[0].status").value(404))
                .andExpect(jsonPath("$.errors[0].code").value("resource_not_found"))
                .andExpect(jsonPath("$.errors[0].title").value("Resource Not Found"))
                .andExpect(jsonPath("$.errors[0].detail").value("Could not find TENANT with ID '" + nonExistentId + "'."));
    }

    @Test
    @DisplayName("Check updateTenant (PUT) - Forbidden for non Super Admin")
    void updateTenantStatus_Forbidden_NonSuperAdmin() throws Exception {
        // Act & Assert
        this.mockMvc.perform(patch(BASE_URL+ "/tenants/" + TEST_TENANT_ID + "/status")
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        this.mockMvc.perform(patch(BASE_URL+ "/tenants/" + TEST_TENANT_ID + "/status")
                        .header(HttpHeaders.AUTHORIZATION, EMPLOYEE_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}