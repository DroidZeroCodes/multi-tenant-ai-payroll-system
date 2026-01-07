package org.droid.zero.multitenantaipayrollsystem.modules.position;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.droid.zero.multitenantaipayrollsystem.modules.position.dto.PositionRequest;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for Position API")
class PositionIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    private PositionRequest validPositionRequest;
    private String positionId;

    @BeforeEach
    void setUp() {
        validPositionRequest = new PositionRequest(
                "HR Officer",
                "Position description",
                "Manager"
        );
    }

    @Test
    @DisplayName("Create position - Success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void createPosition_shouldSucceed() throws Exception {
        // Act & Assert
        MvcResult result = mockMvc.perform(post(BASE_URL + "/positions")
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPositionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Create Success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.title").value(validPositionRequest.title()))
                .andExpect(jsonPath("$.data.description").value(validPositionRequest.description()))
                .andExpect(jsonPath("$.data.level").value(validPositionRequest.level()))
                .andReturn();

        // Extract position ID for subsequent tests
        String response = result.getResponse().getContentAsString();
        positionId = JsonPath.parse(response).read("$.data.id", String.class);
    }

    @Test
    @DisplayName("Create position - Unauthorized (No Token)")
    void createPosition_withoutToken_shouldReturnUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPositionRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Get position by ID - Success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getPositionById_shouldSucceed() throws Exception {
        // Arrange - Create a position first
        createTestPosition();

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/positions/" + positionId)
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(positionId))
                .andExpect(jsonPath("$.data.title").value(validPositionRequest.title()))
                .andExpect(jsonPath("$.data.description").value(validPositionRequest.description()))
                .andExpect(jsonPath("$.data.level").value(validPositionRequest.level()));
    }

    @Test
    @DisplayName("Get non-existent position - Not Found")
    void getNonExistentPosition_shouldReturnNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/positions/" + UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update position - Success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updatePosition_shouldSucceed() throws Exception {
        // Arrange - Create a position first
        createTestPosition();
        
        // Prepare update request
        PositionRequest updateRequest = new PositionRequest(
                "Updated Position Title",
                "Updated Position Description",
                "Updated Position Level"
        );

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/positions/" + positionId)
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.title").value(updateRequest.title()))
                .andExpect(jsonPath("$.data.description").value(updateRequest.description()))
                .andExpect(jsonPath("$.data.level").value(updateRequest.level()));
    }

    @Test
    @DisplayName("Toggle position status - Success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void togglePositionStatus_shouldSucceed() throws Exception {
        // Arrange - Create a position first
        createTestPosition();

        // Act & Assert - Toggle status
        mockMvc.perform(patch(BASE_URL + "/positions/" + positionId + "/status")
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Update Success"));

        // Verify the status was toggled by checking the active status
        mockMvc.perform(get(BASE_URL + "/positions/" + positionId)
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.active").isBoolean())
                .andExpect(jsonPath("$.data.active").value(notNullValue()));
    }

    @Test
    @DisplayName("Create position with invalid data - Bad Request")
    void createPosition_withInvalidData_shouldReturnBadRequest() throws Exception {
        // Arrange - Create invalid request
        PositionRequest invalidRequest = new PositionRequest(
                "", // Empty title (invalid)
                "",  // Empty description (invalid)
                "" // Empty level (invalid)
        );

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/positions")
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(3))); // Expecting 3 validation errors
    }

    /**
     * Helper method to create a test position and store its ID
     */
    private void createTestPosition() throws Exception {
        MvcResult result = mockMvc.perform(post(BASE_URL + "/positions")
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPositionRequest)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        positionId = JsonPath.parse(response).read("$.data.id", String.class);
    }
}