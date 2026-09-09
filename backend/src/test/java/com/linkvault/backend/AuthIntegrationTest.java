package com.linkvault.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AuthIntegrationTest extends IntegrationTestBase {

    @Test
    void registerAndLoginShouldWork() throws Exception {

        String email = uniqueEmail("bharat");

        String registerJson = """
                {
                    "name": "Bharat",
                    "email": "%s",
                    "password": "Password@123"
                }
                """.formatted(email);

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(email));

        String loginJson = """
                {
                    "email": "%s",
                    "password": "Password@123"
                }
                """.formatted(email);

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    void duplicateEmailShouldReturnConflict() throws Exception {

        String email = uniqueEmail("duplicate");

        String json = """
                {
                    "name": "Duplicate Test",
                    "email": "%s",
                    "password": "Password@123"
                }
                """.formatted(email);

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void protectedEndpointWithoutJwtShouldReturnUnauthorized()
            throws Exception {

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validJwtShouldAccessProtectedEndpoint()
            throws Exception {

        String email = uniqueEmail("protected");
        String token = registerAndLogin("Protected User", email);

        mockMvc.perform(
                get("/api/users/me")
                        .header("Authorization", auth(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email));
    }
}