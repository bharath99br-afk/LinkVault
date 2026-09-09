package com.linkvault.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ResourceOwnershipIntegrationTest extends IntegrationTestBase {

    @Test
    void userShouldNotAccessAnotherUsersCard() throws Exception {

        String tokenA =
                registerAndLogin("User A", uniqueEmail("user-a"));

        String tokenB =
                registerAndLogin("User B", uniqueEmail("user-b"));

        long bankId =
                createBank(tokenA, "Ownership Bank");

        long cardId =
                createCard(tokenA, "User A Card", bankId);

        mockMvc.perform(
                get("/api/cards/" + cardId)
                        .header("Authorization", auth(tokenB)))
                .andExpect(status().isNotFound());
    }

    @Test
    void userShouldNotAttachAnotherUsersMerchantToLink()
            throws Exception {

        String tokenA =
                registerAndLogin("User A", uniqueEmail("merchant-a"));

        String tokenB =
                registerAndLogin("User B", uniqueEmail("merchant-b"));

        long merchantId =
                createMerchant(tokenA, "Private Merchant", null);

        String json = """
                {
                    "title": "Cross User Link",
                    "url": "https://example.com",
                    "merchantId": %d
                }
                """.formatted(merchantId);

        mockMvc.perform(
                post("/api/links")
                        .header("Authorization", auth(tokenB))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }
}