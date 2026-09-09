package com.linkvault.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class OfferIntegrationTest extends IntegrationTestBase {

    @Test
    void offerShouldBeEligibleForMatchingBank()
            throws Exception {

        String token =
                registerAndLogin(
                        "Offer User",
                        uniqueEmail("offer-bank"));

        long bankId =
                createBank(token, "HDFC Bank");

        long cardId =
                createCard(
                        token,
                        "HDFC Regalia",
                        bankId);

        long offerId =
                createOffer(
                        token,
                        "10 Percent HDFC Offer",
                        new BigDecimal("10"),
                        null);

        String applicabilityJson = """
                {
                    "bankId": %d
                }
                """.formatted(bankId);

        mockMvc.perform(
                post("/api/offers/" + offerId + "/banks")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(applicabilityJson))
                .andExpect(status().isOk());

        String eligibilityJson = """
                {
                    "cardId": %d,
                    "transactionAmount": 1000
                }
                """.formatted(cardId);

        mockMvc.perform(
                post("/api/offers/" + offerId + "/eligibility")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eligibilityJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.eligible").value(true));
    }

    @Test
    void offerCalculationShouldCalculatePercentageDiscount()
            throws Exception {

        String token =
                registerAndLogin(
                        "Calculation User",
                        uniqueEmail("calculation"));

        long bankId =
                createBank(token, "ICICI Bank");

        long cardId =
                createCard(
                        token,
                        "ICICI Amazon Card",
                        bankId);

        long offerId =
                createOffer(
                        token,
                        "10 Percent Offer",
                        new BigDecimal("10"),
                        null);

        String calculationJson = """
                {
                    "cardId": %d,
                    "transactionAmount": 1000
                }
                """.formatted(cardId);

        mockMvc.perform(
                post("/api/offers/" + offerId + "/calculate")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(calculationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.eligible").value(true))
                .andExpect(
                        jsonPath("$.data.discountAmount")
                                .value(100.00))
                .andExpect(
                        jsonPath("$.data.finalAmount")
                                .value(900.00))
                .andExpect(
                        jsonPath("$.data.savingsPercentage")
                                .value(10.00));
    }

    @Test
    void minimumTransactionAmountShouldMakeOfferIneligible()
            throws Exception {

        String token =
                registerAndLogin(
                        "Minimum User",
                        uniqueEmail("minimum"));

        long bankId =
                createBank(token, "Axis Bank");

        long cardId =
                createCard(
                        token,
                        "Axis Neo",
                        bankId);

        String offerJson = """
                {
                    "title": "Minimum Spend Offer",
                    "description": "Minimum spend test",
                    "discountType": "PERCENTAGE",
                    "discountValue": 10,
                    "minTransactionAmount": 5000,
                    "startDate": "%s",
                    "endDate": "%s"
                }
                """.formatted(
                        java.time.LocalDate.now().minusDays(1),
                        java.time.LocalDate.now().plusDays(30));

        var result = mockMvc.perform(
                post("/api/offers")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(offerJson))
                .andExpect(status().isCreated())
                .andReturn();

        long offerId = extractId(result);

        String eligibilityJson = """
                {
                    "cardId": %d,
                    "transactionAmount": 1000
                }
                """.formatted(cardId);

        mockMvc.perform(
                post("/api/offers/" + offerId + "/eligibility")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eligibilityJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.eligible").value(false))
                .andExpect(jsonPath("$.data.reason")
    .value("Minimum transaction amount is 5000.00"));
    }

    @Test
    void duplicateBankApplicabilityShouldReturnConflict()
            throws Exception {

        String token =
                registerAndLogin(
                        "Duplicate Applicability",
                        uniqueEmail("applicability"));

        long bankId =
                createBank(token, "SBI");

        long offerId =
                createOffer(
                        token,
                        "SBI Offer",
                        new BigDecimal("5"),
                        null);

        String json = """
                {
                    "bankId": %d
                }
                """.formatted(bankId);

        mockMvc.perform(
                post("/api/offers/" + offerId + "/banks")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(
                post("/api/offers/" + offerId + "/banks")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }
}