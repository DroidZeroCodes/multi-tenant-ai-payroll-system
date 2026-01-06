package org.droid.zero.multitenantaipayrollsystem.modules.department;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.droid.zero.multitenantaipayrollsystem.modules.department.dto.DepartmentRequest;
import org.droid.zero.multitenantaipayrollsystem.test.config.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Integration tests for Department API")
class DepartmentIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    private DepartmentRequest validDepartmentRequest;
    private String departmentId;

    @BeforeEach
    void setUp() {
        validDepartmentRequest = new DepartmentRequest(
                "Engineering",
                "Engineering Department"
        );
    }

    @Test
    @DisplayName("Create department - Success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void createDepartment_shouldSucceed() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(post(BASE_URL + "/departments")
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDepartmentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Create Success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(validDepartmentRequest.name()))
                .andExpect(jsonPath("$.data.description").value(validDepartmentRequest.description()))
                .andReturn();

        // Extract department ID for subsequent tests
        String response = result.getResponse().getContentAsString();
        departmentId = JsonPath.parse(response).read("$.data.id", String.class);
    }

    @Test
    @DisplayName("Create department - Unauthorized (No Token)")
    void createDepartment_withoutToken_shouldReturnUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDepartmentRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get department by ID - Success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getDepartmentById_shouldSucceed() throws Exception {
        // Arrange - Create a department first
        createTestDepartment();

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/departments/" + departmentId)
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(departmentId))
                .andExpect(jsonPath("$.data.name").value(validDepartmentRequest.name()));
    }

    @Test
    @DisplayName("Get non-existent department - Not Found")
    void getNonExistentDepartment_shouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/departments/" + UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update department - Success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateDepartment_shouldSucceed() throws Exception {
        // Arrange - Create a department first
        createTestDepartment();
        
        // Prepare update request
        DepartmentRequest updateRequest = new DepartmentRequest(
                "Updated Engineering",
                "Updated Engineering Department"
        );

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/departments/" + departmentId)
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.name").value(updateRequest.name()))
                .andExpect(jsonPath("$.data.description").value(updateRequest.description()));
    }

    @Test
    @DisplayName("Toggle department status - Success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void toggleDepartmentStatus_shouldSucceed() throws Exception {
        // Arrange - Create a department first
        createTestDepartment();

        // Act & Assert - Toggle status
        mockMvc.perform(patch(BASE_URL + "/departments/" + departmentId + "/status")
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Update Success"));

        // Verify the status was toggled by checking the active status
        mockMvc.perform(get(BASE_URL + "/departments/" + departmentId)
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.active").isBoolean())
                .andExpect(jsonPath("$.data.active").value(notNullValue()));
    }

    @Test
    @DisplayName("Create department with invalid data - Bad Request")
    void createDepartment_withInvalidData_shouldReturnBadRequest() throws Exception {
        // Arrange - Create invalid request
        DepartmentRequest invalidRequest = new DepartmentRequest(
                "", // Empty name (invalid)
                ""  // Empty description (invalid)
        );

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/departments")
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(2))); // Expecting 2 validation errors (name and description)
    }

    /**
     * Helper method to create a test department and store its ID
     */
    private void createTestDepartment() throws Exception {
        MvcResult result = mockMvc.perform(post(BASE_URL + "/departments")
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDepartmentRequest)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        departmentId = JsonPath.parse(response).read("$.data.id", String.class);
    }
}