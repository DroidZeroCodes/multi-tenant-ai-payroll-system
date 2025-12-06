package org.droid.zero.multitenantaipayrollsystem.tenant;

import org.droid.zero.multitenantaipayrollsystem.BaseIntegrationTest;
import org.droid.zero.multitenantaipayrollsystem.tenant.dto.CreateTenantRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @DisplayName("Check addTenant with valid input (POST)")
    void testAddTenant_Success() throws Exception {
        //Arrange
        CreateTenantRequest request = new CreateTenantRequest(
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
                .andExpect(jsonPath("$.message").value("Tenant Creation Success"))
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
        CreateTenantRequest request = new CreateTenantRequest(
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

        CreateTenantRequest request = new CreateTenantRequest(
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
}