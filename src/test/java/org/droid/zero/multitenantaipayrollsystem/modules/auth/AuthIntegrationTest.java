package org.droid.zero.multitenantaipayrollsystem.modules.auth;

import org.apache.http.HttpHeaders;
import org.droid.zero.multitenantaipayrollsystem.test.config.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for Authentication")
class AuthIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Check login endpoint returns token (POST)")
    void login_shouldReturnToken() throws Exception {
        // Act & Assert
        this.mockMvc.perform(post(this.getBaseUrl() + "/auth/login")
                        .with(httpBasic("superAdmin.default@email.com", "password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User Info and JSON Web Token"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.token").isString())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check logout endpoint invalidates token (DELETE)")
    void logout_shouldInvalidateToken() throws Exception {
        // Act & Assert
        this.mockMvc.perform(delete(this.getBaseUrl() + "/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, TENANT_ADMIN_TOKEN)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User has been logged out"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("Check logout without authorization header (DELETE)")
    void logout_withoutAuthHeader_shouldReturnError() throws Exception {
        // Act & Assert
        this.mockMvc.perform(delete(this.getBaseUrl() + "/auth/logout")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // Or 403 depending on your security config
    }

    @Test
    @DisplayName("Check logout with invalid token format (DELETE)")
    void logout_withInvalidTokenFormat_shouldReturnError() throws Exception {
        // Act & Assert
        this.mockMvc.perform(delete(this.getBaseUrl() + "/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "InvalidTokenFormat")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Check logout with empty bearer token (DELETE)")
    void logout_withEmptyBearerToken_shouldReturnError() throws Exception {
        // Act & Assert
        this.mockMvc.perform(delete(this.getBaseUrl() + "/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Check login with invalid credentials returns unauthorized")
    void login_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        // Act & Assert
        this.mockMvc.perform(post(this.getBaseUrl() + "/auth/login")
                        .header(HttpHeaders.AUTHORIZATION, "Basic invalid:credentials")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Check login with non-existent user returns unauthorized")
    void login_withNonExistentUser_shouldReturnUnauthorized() throws Exception {
        // Act & Assert
        this.mockMvc.perform(post(this.getBaseUrl() + "/auth/login")
                        .header(HttpHeaders.AUTHORIZATION, "Basic nonexistent@email.com:password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Check logout after successful login flow")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void loginAndLogout_workflow() throws Exception {
        // Login to get a fresh token
        String responseContent = this.mockMvc.perform(post(this.getBaseUrl() + "/auth/login")
                        .with(httpBasic("tenantAdmin.default@email.com", "password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract token from response
        String token = objectMapper.readTree(responseContent)
                .path("data")
                .path("token")
                .asText();

        //Use the token to log out
        this.mockMvc.perform(delete(this.getBaseUrl() + "/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User has been logged out"));
    }

    @Test
    @DisplayName("Check multiple logout attempts with same token")
    void multipleLogouts_withSameToken_shouldHandleGracefully() throws Exception {
        String result = this.mockMvc.perform(post(this.getBaseUrl() + "/auth/login")
                        .with(httpBasic("employee.default@email.com", "password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(result)
                .path("data")
                .path("token")
                .asText();

        // First logout should succeed
        this.mockMvc.perform(delete(this.getBaseUrl() + "/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Second logout with same token should return error
        this.mockMvc.perform(delete(this.getBaseUrl() + "/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}