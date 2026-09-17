package com.linkvault.backend.card;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import com.linkvault.backend.IntegrationTestBase;

class CardCatalogIntegrationTest extends IntegrationTestBase {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void createCardWithMatchingCardProductShouldWork()
            throws Exception {

        String token = registerAndLogin(
                "Catalog User",
                uniqueEmail("catalog"));

        long bankId = createBank(token, "HDFC Bank");

        long cardProductId = createCardProduct(
                bankId,
                "HDFC Regalia",
                "CREDIT",
                true);

        String json = """
                {
                    "name": "HDFC Regalia",
                    "lastFourDigits": "1234",
                    "cardType": "CREDIT",
                    "bankId": %d,
                    "cardProductId": %d
                }
                """.formatted(bankId, cardProductId);

        mockMvc.perform(
                post("/api/cards")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.bankId").value(bankId))
                .andExpect(jsonPath("$.data.cardProductId")
                        .value(cardProductId))
                .andExpect(jsonPath("$.data.cardProductName")
                        .value("HDFC Regalia"));
    }

    @Test
    void createCustomCardWithoutCardProductShouldStillWork()
            throws Exception {

        String token = registerAndLogin(
                "Custom Card User",
                uniqueEmail("custom"));

        long bankId = createBank(token, "Custom Bank");

        String json = """
                {
                    "name": "My Custom Card",
                    "lastFourDigits": "5678",
                    "cardType": "CREDIT",
                    "bankId": %d,
                    "cardProductId": null
                }
                """.formatted(bankId);

        mockMvc.perform(
                post("/api/cards")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.bankId").value(bankId))
                .andExpect(jsonPath("$.data.cardProductId").doesNotExist());
    }

    @Test
    void createCardWithCardProductFromDifferentBankShouldFail()
            throws Exception {

        String token = registerAndLogin(
                "Mismatch User",
                uniqueEmail("mismatch"));

        long hdfcBankId = createBank(token, "HDFC Bank");
        long iciciBankId = createBank(token, "ICICI Bank");

        long iciciProductId = createCardProduct(
                iciciBankId,
                "ICICI Platinum",
                "CREDIT",
                true);

        String json = """
                {
                    "name": "Invalid HDFC Card",
                    "lastFourDigits": "1111",
                    "cardType": "CREDIT",
                    "bankId": %d,
                    "cardProductId": %d
                }
                """.formatted(hdfcBankId, iciciProductId);

        mockMvc.perform(
                post("/api/cards")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createCardWithInactiveCardProductShouldFail()
            throws Exception {

        String token = registerAndLogin(
                "Inactive Product User",
                uniqueEmail("inactive"));

        long bankId = createBank(token, "HDFC Bank");

        long cardProductId = createCardProduct(
                bankId,
                "HDFC Inactive Card",
                "CREDIT",
                false);

        String json = """
                {
                    "name": "HDFC Inactive Card",
                    "lastFourDigits": "2222",
                    "cardType": "CREDIT",
                    "bankId": %d,
                    "cardProductId": %d
                }
                """.formatted(bankId, cardProductId);

        mockMvc.perform(
                post("/api/cards")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createCardWithNonexistentCardProductShouldFail()
            throws Exception {

        String token = registerAndLogin(
                "Missing Product User",
                uniqueEmail("missing"));

        long bankId = createBank(token, "HDFC Bank");

        String json = """
                {
                    "name": "Unknown Product Card",
                    "lastFourDigits": "3333",
                    "cardType": "CREDIT",
                    "bankId": %d,
                    "cardProductId": 999999
                }
                """.formatted(bankId);

        mockMvc.perform(
                post("/api/cards")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void updateCardWithMatchingCardProductShouldWork()
            throws Exception {

        String token = registerAndLogin(
                "Update User",
                uniqueEmail("update"));

        long bankId = createBank(token, "HDFC Bank");

        long firstProductId = createCardProduct(
                bankId,
                "HDFC Regalia",
                "CREDIT",
                true);

        long secondProductId = createCardProduct(
                bankId,
                "HDFC Millennia",
                "CREDIT",
                true);

        String createJson = """
                {
                    "name": "HDFC Regalia",
                    "lastFourDigits": "4444",
                    "cardType": "CREDIT",
                    "bankId": %d,
                    "cardProductId": %d
                }
                """.formatted(bankId, firstProductId);

        long cardId = extractId(
                mockMvc.perform(
                        post("/api/cards")
                                .header("Authorization", auth(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createJson))
                        .andExpect(status().isCreated())
                        .andReturn());

        String updateJson = """
                {
                    "name": "HDFC Millennia",
                    "lastFourDigits": "4444",
                    "cardType": "CREDIT",
                    "bankId": %d,
                    "cardProductId": %d
                }
                """.formatted(bankId, secondProductId);

        mockMvc.perform(
                put("/api/cards/" + cardId)
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cardProductId")
                        .value(secondProductId))
                .andExpect(jsonPath("$.data.cardProductName")
                        .value("HDFC Millennia"));
    }

    @Test
    void updateCardWithDifferentBankProductShouldFail()
            throws Exception {

        String token = registerAndLogin(
                "Update Mismatch User",
                uniqueEmail("updatemismatch"));

        long hdfcBankId = createBank(token, "HDFC Bank");
        long iciciBankId = createBank(token, "ICICI Bank");

        long hdfcProductId = createCardProduct(
                hdfcBankId,
                "HDFC Regalia",
                "CREDIT",
                true);

        long iciciProductId = createCardProduct(
                iciciBankId,
                "ICICI Platinum",
                "CREDIT",
                true);

        String createJson = """
                {
                    "name": "HDFC Regalia",
                    "lastFourDigits": "5555",
                    "cardType": "CREDIT",
                    "bankId": %d,
                    "cardProductId": %d
                }
                """.formatted(hdfcBankId, hdfcProductId);

        long cardId = extractId(
                mockMvc.perform(
                        post("/api/cards")
                                .header("Authorization", auth(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createJson))
                        .andExpect(status().isCreated())
                        .andReturn());

        String invalidUpdateJson = """
                {
                    "name": "Invalid ICICI Product",
                    "lastFourDigits": "5555",
                    "cardType": "CREDIT",
                    "bankId": %d,
                    "cardProductId": %d
                }
                """.formatted(hdfcBankId, iciciProductId);

        mockMvc.perform(
                put("/api/cards/" + cardId)
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUpdateJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void existingCardWithoutCardProductShouldStillWork()
            throws Exception {

        String token = registerAndLogin(
                "Legacy Card User",
                uniqueEmail("legacy"));

        long bankId = createBank(token, "Legacy Bank");

        long cardId = createCard(
                token,
                "Legacy Custom Card",
                bankId);

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/cards/" + cardId)
                        .header("Authorization", auth(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cardProductId")
                        .doesNotExist());
    }

    private long createCardProduct(
            long bankId,
            String name,
            String cardType,
            boolean active) {

        return jdbcTemplate.queryForObject(
                """
                        INSERT INTO card_products (
                            bank_id,
                            name,
                            card_type,
                            active
                        )
                        VALUES (?, ?, ?, ?)
                        RETURNING id
                        """,
                Long.class,
                bankId,
                name,
                cardType,
                active);
    }
}