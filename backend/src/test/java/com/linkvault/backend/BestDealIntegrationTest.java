package com.linkvault.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class BestDealIntegrationTest extends IntegrationTestBase {

    @Test
    void merchantSpecificOfferShouldBeatGeneralOffer()
            throws Exception {

        String token =
                registerAndLogin(
                        "Best Deal User",
                        uniqueEmail("best-deal"));

        long bankId =
                createBank(token, "HDFC");

        createCard(
                token,
                "HDFC Card",
                bankId);

        long amazonGlobalMerchant =
                createGlobalMerchant(
                        token,
                        "Amazon");

        long amazonMerchant =
                createMerchant(
                        token,
                        "Amazon",
                        amazonGlobalMerchant);

        long productId =
                createProduct(
                        token,
                        "Laptop",
                        amazonMerchant);

        long linkId =
                createLink(
                        token,
                        "Laptop Purchase",
                        amazonMerchant,
                        productId);

        // General offer: 5%
        createOffer(
                token,
                "General 5 Percent",
                new BigDecimal("5"),
                null);

        // Amazon-specific offer: 20%
        createOffer(
                token,
                "Amazon 20 Percent",
                new BigDecimal("20"),
                amazonGlobalMerchant);

        String request = """
                {
                    "linkId": %d,
                    "transactionAmount": 10000
                }
                """.formatted(linkId);

        mockMvc.perform(
                post("/api/deals/best")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data.bestDeal.offerTitle")
                                .value("Amazon 20 Percent"))
                .andExpect(
                        jsonPath("$.data.bestDeal.discountAmount")
                                .value(2000.00))
                .andExpect(
                        jsonPath("$.data.bestDeal.finalAmount")
                                .value(8000.00))
                .andExpect(
                        jsonPath("$.data.alternatives[0].offerTitle")
                                .value("General 5 Percent"));
    }

    @Test
    void unrelatedMerchantOfferShouldBeExcluded()
            throws Exception {

        String token =
                registerAndLogin(
                        "Merchant Filter User",
                        uniqueEmail("merchant-filter"));

        long bankId =
                createBank(token, "ICICI");

        createCard(
                token,
                "ICICI Card",
                bankId);

        long amazonGlobal =
                createGlobalMerchant(token, "Amazon");

        long flipkartGlobal =
                createGlobalMerchant(token, "Flipkart");

        long amazonMerchant =
                createMerchant(
                        token,
                        "Amazon",
                        amazonGlobal);

        long productId =
                createProduct(
                        token,
                        "Headphones",
                        amazonMerchant);

        long linkId =
                createLink(
                        token,
                        "Headphones",
                        amazonMerchant,
                        productId);

        createOffer(
                token,
                "Amazon 10 Percent",
                new BigDecimal("10"),
                amazonGlobal);

        createOffer(
                token,
                "Flipkart 50 Percent",
                new BigDecimal("50"),
                flipkartGlobal);

        String request = """
                {
                    "linkId": %d,
                    "transactionAmount": 10000
                }
                """.formatted(linkId);

        mockMvc.perform(
                post("/api/deals/best")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data.bestDeal.offerTitle")
                                .value("Amazon 10 Percent"))
                .andExpect(jsonPath("$.data.alternatives.size()").value(0));
    }

    @Test
    void amountOnlyBestDealShouldStillWork()
            throws Exception {

        String token =
                registerAndLogin(
                        "Amount Only User",
                        uniqueEmail("amount-only"));

        long bankId =
                createBank(token, "Axis");

        createCard(
                token,
                "Axis Card",
                bankId);

        createOffer(
                token,
                "General Offer",
                new BigDecimal("10"),
                null);

        String request = """
                {
                    "transactionAmount": 5000
                }
                """;

        mockMvc.perform(
                post("/api/deals/best")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data.bestDeal.offerTitle")
                                .value("General Offer"))
                .andExpect(
                        jsonPath("$.data.bestDeal.finalAmount")
                                .value(4500.00));
    }

    @Test
    void crossUserLinkShouldReturnNotFound()
            throws Exception {

        String ownerToken =
                registerAndLogin(
                        "Owner",
                        uniqueEmail("owner"));

        String attackerToken =
                registerAndLogin(
                        "Attacker",
                        uniqueEmail("attacker"));

        long linkId =
                createLink(
                        ownerToken,
                        "Private Purchase",
                        null,
                        null);

        String request = """
                {
                    "linkId": %d,
                    "transactionAmount": 1000
                }
                """.formatted(linkId);

        mockMvc.perform(
                post("/api/deals/best")
                        .header(
                                "Authorization",
                                auth(attackerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound());
    }

    @Test
    void noEligibleDealsShouldReturnNullBestDeal()
            throws Exception {

        String token =
                registerAndLogin(
                        "No Deal User",
                        uniqueEmail("no-deal"));

        long bankId =
                createBank(token, "Bank A");

        createCard(
                token,
                "Bank A Card",
                bankId);

        String offerJson = """
                {
                    "title": "Future Offer",
                    "description": "Inactive offer",
                    "discountType": "PERCENTAGE",
                    "discountValue": 50,
                    "startDate": "%s",
                    "endDate": "%s"
                }
                """.formatted(
                        java.time.LocalDate.now().plusDays(1),
                        java.time.LocalDate.now().plusDays(30));

        mockMvc.perform(
                post("/api/offers")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(offerJson))
                .andExpect(status().isCreated());

        String request = """
                {
                    "transactionAmount": 1000
                }
                """;

        mockMvc.perform(
                post("/api/deals/best")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bestDeal").doesNotExist())
                .andExpect(jsonPath("$.data.alternatives.size()").value(0));
    }
}