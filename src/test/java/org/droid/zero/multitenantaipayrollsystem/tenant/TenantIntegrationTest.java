package org.droid.zero.multitenantaipayrollsystem.tenant;

import org.droid.zero.multitenantaipayrollsystem.BaseIntegrationTest;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.TenantRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for Tenant")
class TenantIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private TenantRepository tenantRepository;

    @BeforeEach
    void setup() {

    }

    @Test
    @DisplayName("Check findTenantById (GET)")
    void findTenantById() throws Exception {
        //Arrange
        Tenant tenant = new Tenant();
        tenant.setName("Test Tenant");
        tenant.setEmail("testEmail@email.com");
        tenant.setPhone("1234567890");
        tenant.setIndustry("technology");
        tenant = tenantRepository.save(tenant);

        //Act & Assert
        this.mockMvc.perform(get(this.getBaseUrl()+ "/tenants/" + tenant.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.name").value("Test Tenant"))
                .andExpect(jsonPath("$.data.email").value("testEmail@email.com"))
                .andExpect(jsonPath("$.data.phone").value("1234567890"))
                .andExpect(jsonPath("$.data.industry").value("technology"))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.errors").isEmpty());
    }


    @Test
    @DisplayName("Check findTenantById with not found (GET)")
    void findTenantById_NotFound() throws Exception {
        //Arrange
        UUID nonExistentId = UUID.randomUUID();

        //Act & Assert
        this.mockMvc.perform(get(this.getBaseUrl()+ "/tenants/" + nonExistentId)
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
    @DisplayName("Check addTenant with valid input (POST)")
    void testAddTenant_Success() throws Exception {
        //Arrange
        TenantRequest request = new TenantRequest(
                "testName",
                "testEmail@email.com",
                "1234567890",
                "technology"
        );

        //Act & Assert
        this.mockMvc.perform(post(this.getBaseUrl()+ "/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Create Success"))
                .andExpect(jsonPath("$.data.name").value("testName"))
                .andExpect(jsonPath("$.data.email").value("testEmail@email.com"))
                .andExpect(jsonPath("$.data.phone").value("1234567890"))
                .andExpect(jsonPath("$.data.industry").value("technology"))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check addTenant with invalid input (POST)")
    void testAddTenant_InvalidInput() throws Exception {
        //Arrange
        TenantRequest request = new TenantRequest(
                "",
                "invalidEmail",
                "1234567890",
                "technology"
        );

        //Act & Assert
        this.mockMvc.perform(post(this.getBaseUrl()+ "/tenants")
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
                        containsInAnyOrder("invalid email format", "tenant name is required")));
    }

    @Test
    @DisplayName("Check addTenant with unique constrain violation (POST)")
    void testAddTenant_UniqueConstraintViolation() throws Exception {
        //Arrange
        Tenant existingTenant = new Tenant();
        existingTenant.setName("existing");
        existingTenant.setEmail("existing@email.com");
        existingTenant.setPhone("11111111");
        existingTenant.setIndustry("existing");
        tenantRepository.save(existingTenant);

        TenantRequest request = new TenantRequest(
                "existing",
                "existing@email.com",
                "11111111",
                "existing"
        );

        //Act & Assert
        this.mockMvc.perform(post(this.getBaseUrl()+ "/tenants")
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
    @DisplayName("Check updateTenant with valid input (PUT)")
    void testUpdateTenant_Success() throws Exception {
        //Arrange
        Tenant existingTenant = new Tenant();
        existingTenant.setName("existingTenantName");
        existingTenant.setEmail("existing@email.com");
        existingTenant.setPhone("11111111111");
        existingTenant.setIndustry("technology");
        tenantRepository.save(existingTenant);

        TenantRequest request = new TenantRequest(
                "newName",
                "new@email.com",
                "9876543210",
                "technology"
        );

        //Act & Assert
        this.mockMvc.perform(put(this.getBaseUrl()+ "/tenants/" + existingTenant.getId())
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
    }

    @Test
    @DisplayName("Check updateTenant with not found (PUT)")
    void testUpdateTenant_NotFound() throws Exception {
        //Arrange
        UUID nonExistentId = UUID.randomUUID();
        TenantRequest request = new TenantRequest(
                "newName",
                "new@email.com",
                "9876543210",
                "technology"
        );

        //Act & Assert
        this.mockMvc.perform(put(this.getBaseUrl()+ "/tenants/" + nonExistentId)
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
    void testUpdateTenant_UniqueConstraintViolation() throws Exception {
        //Arrange
        Tenant existingTenant = new Tenant();
        existingTenant.setName("existingTenantName");
        existingTenant.setEmail("existing@email.com");
        existingTenant.setPhone("11111111111");
        existingTenant.setIndustry("technology");
        tenantRepository.save(existingTenant);

        Tenant existingAnotherTenant = new Tenant();
        existingAnotherTenant.setName("anotherTenantName");
        existingAnotherTenant.setEmail("another@email.com");
        existingAnotherTenant.setPhone("9876543210");
        existingAnotherTenant.setIndustry("technology");
        tenantRepository.save(existingAnotherTenant);

        TenantRequest request = new TenantRequest(
                "anotherTenantName",
                "another@email.com",
                "9876543210",
                "technology"
        );

        //Act & Assert
        this.mockMvc.perform(put(this.getBaseUrl()+ "/tenants/" + existingTenant.getId())
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
    @DisplayName("Check updateTenantStatus with valid input (PATCH)")
    void testUpdateTenantStatus_Success() throws Exception {
        //Arrange
        Tenant existingTenant = new Tenant();
        existingTenant.setName("testTenantName");
        existingTenant.setEmail("testEmail@email.com");
        existingTenant.setPhone("1234567890");
        existingTenant.setIndustry("technology");
        tenantRepository.save(existingTenant);

        //Act & Assert
        this.mockMvc.perform(patch(this.getBaseUrl()+ "/tenants/" + existingTenant.getId() + "/status")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(existingTenant.getId().toString()))
                .andExpect(jsonPath("$.data.active").value(false))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check updateTenantStatus with not found (PATCH)")
    void testUpdateTenantStatus_NotFound() throws Exception {
        //Arrange
        UUID nonExistentId = UUID.randomUUID();

        //Act & Assert
        this.mockMvc.perform(patch(this.getBaseUrl()+ "/tenants/" + nonExistentId + "/status")
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
}