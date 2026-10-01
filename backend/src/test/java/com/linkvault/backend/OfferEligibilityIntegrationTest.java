package com.linkvault.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class OfferEligibilityIntegrationTest extends IntegrationTestBase {

    @Test
    void unrestrictedActiveOfferShouldBeEligible()
            throws Exception {

        String token = registerAndLogin(
                "Unrestricted User",
                uniqueEmail("unrestricted"));

        long bankId = createBank(
                token,
                "HDFC Bank");

        long cardId = createCard(
                token,
                "HDFC Custom Card",
                bankId);

        long offerId = createOffer(
                token,
                "General 10 Percent Offer",
                new BigDecimal("10"),
                null);

        checkEligibility(
                token,
                offerId,
                cardId,
                new BigDecimal("1000"))
                .andExpect(jsonPath("$.data.eligible")
                        .value(true));
    }

    @Test
    void inactiveOfferShouldBeIneligible()
            throws Exception {

        String token = registerAndLogin(
                "Inactive Offer User",
                uniqueEmail("inactiveoffer"));

        long bankId = createBank(
                token,
                "HDFC Bank");

        long cardId = createCard(
                token,
                "HDFC Custom Card",
                bankId);

        String offerJson = """
                {
                    "title": "Expired Offer",
                    "description": "Expired integration test offer",
                    "discountType": "PERCENTAGE",
                    "discountValue": 10,
                    "startDate": "%s",
                    "endDate": "%s"
                }
                """.formatted(
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(1));

        long offerId = createOfferFromJson(
                token,
                offerJson);

        checkEligibility(
                token,
                offerId,
                cardId,
                new BigDecimal("1000"))
                .andExpect(jsonPath("$.data.eligible")
                        .value(false))
                .andExpect(jsonPath("$.data.reason")
                        .value("Offer is not currently active"));
    }

    @Test
    void minimumTransactionAmountShouldBeEnforced()
            throws Exception {

        String token = registerAndLogin(
                "Minimum Amount User",
                uniqueEmail("minimum"));

        long bankId = createBank(
                token,
                "HDFC Bank");

        long cardId = createCard(
                token,
                "HDFC Custom Card",
                bankId);

        String offerJson = """
                {
                    "title": "Minimum Spend Offer",
                    "description": "Minimum spend integration test offer",
                    "discountType": "PERCENTAGE",
                    "discountValue": 10,
                    "minTransactionAmount": 5000,
                    "startDate": "%s",
                    "endDate": "%s"
                }
                """.formatted(
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30));

        long offerId = createOfferFromJson(
                token,
                offerJson);

        checkEligibility(
                token,
                offerId,
                cardId,
                new BigDecimal("4999"))
                .andExpect(jsonPath("$.data.eligible")
                        .value(false))
                .andExpect(jsonPath("$.data.reason")
                        .value("Minimum transaction amount is 5000.00"));

        checkEligibility(
                token,
                offerId,
                cardId,
                new BigDecimal("5000"))
                .andExpect(jsonPath("$.data.eligible")
                        .value(true));
    }

    @Test
    void matchingBankShouldMakeOfferEligible()
            throws Exception {

        String token = registerAndLogin(
                "Bank Match User",
                uniqueEmail("bankmatch"));

        long hdfcBankId = createBank(
                token,
                "HDFC Bank");

        long cardId = createCard(
                token,
                "HDFC Card",
                hdfcBankId);

        long offerId = createOffer(
                token,
                "HDFC Bank Offer",
                new BigDecimal("10"),
                null);

        addBankApplicability(
                token,
                offerId,
                hdfcBankId);

        checkEligibility(
                token,
                offerId,
                cardId,
                new BigDecimal("1000"))
                .andExpect(jsonPath("$.data.eligible")
                        .value(true));
    }

    @Test
    void differentBankShouldMakeOfferIneligible()
            throws Exception {

        String token = registerAndLogin(
                "Bank Mismatch User",
                uniqueEmail("bankmismatch"));

        long hdfcBankId = createBank(
                token,
                "HDFC Bank");

        long iciciBankId = createBank(
                token,
                "ICICI Bank");

        long iciciCardId = createCard(
                token,
                "ICICI Card",
                iciciBankId);

        long offerId = createOffer(
                token,
                "HDFC Only Offer",
                new BigDecimal("10"),
                null);

        addBankApplicability(
                token,
                offerId,
                hdfcBankId);

        checkEligibility(
                token,
                offerId,
                iciciCardId,
                new BigDecimal("1000"))
                .andExpect(jsonPath("$.data.eligible")
                        .value(false))
                .andExpect(jsonPath("$.data.reason")
                        .value("Offer is not applicable to this card"));
    }

    @Test
    void matchingCardProductShouldMakeOfferEligible()
            throws Exception {

        String token = registerAndLogin(
                "Card Product Match User",
                uniqueEmail("cardproductmatch"));

        long hdfcBankId = createBank(
                token,
                "HDFC Bank");

        long regaliaProductId = createCardProduct(
                hdfcBankId,
                "HDFC Regalia",
                "CREDIT",
                true);

        long cardId = createCard(
                token,
                "HDFC Regalia",
                hdfcBankId,
                regaliaProductId);

        long offerId = createOffer(
                token,
                "Regalia Offer",
                new BigDecimal("15"),
                null);

        addCardApplicability(
                token,
                offerId,
                regaliaProductId);

        checkEligibility(
                token,
                offerId,
                cardId,
                new BigDecimal("1000"))
                .andExpect(jsonPath("$.data.eligible")
                        .value(true));
    }

    @Test
    void differentCardProductShouldMakeOfferIneligible()
            throws Exception {

        String token = registerAndLogin(
                "Card Product Mismatch User",
                uniqueEmail("cardproductmismatch"));

        long hdfcBankId = createBank(
                token,
                "HDFC Bank");

        long regaliaProductId = createCardProduct(
                hdfcBankId,
                "HDFC Regalia",
                "CREDIT",
                true);

        long millenniaProductId = createCardProduct(
                hdfcBankId,
                "HDFC Millennia",
                "CREDIT",
                true);

        long cardId = createCard(
                token,
                "HDFC Millennia",
                hdfcBankId,
                millenniaProductId);

        long offerId = createOffer(
                token,
                "Regalia Only Offer",
                new BigDecimal("15"),
                null);

        addCardApplicability(
                token,
                offerId,
                regaliaProductId);

        checkEligibility(
                token,
                offerId,
                cardId,
                new BigDecimal("1000"))
                .andExpect(jsonPath("$.data.eligible")
                        .value(false))
                .andExpect(jsonPath("$.data.reason")
                        .value("Offer is not applicable to this card"));
    }

    @Test
    void combinedBankAndCardProductShouldUseOrSemantics()
            throws Exception {

        String token = registerAndLogin(
                "Combined Applicability User",
                uniqueEmail("combined"));

        long hdfcBankId = createBank(
                token,
                "HDFC Bank");

        long iciciBankId = createBank(
                token,
                "ICICI Bank");

        long regaliaProductId = createCardProduct(
                hdfcBankId,
                "HDFC Regalia",
                "CREDIT",
                true);

        long millenniaProductId = createCardProduct(
                hdfcBankId,
                "HDFC Millennia",
                "CREDIT",
                true);

        long hdfcMillenniaCardId = createCard(
                token,
                "HDFC Millennia",
                hdfcBankId,
                millenniaProductId);

        long iciciCardId = createCard(
                token,
                "ICICI Card",
                iciciBankId);

        long offerId = createOffer(
                token,
                "HDFC Regalia Offer",
                new BigDecimal("20"),
                null);

        addBankApplicability(
                token,
                offerId,
                hdfcBankId);

        addCardApplicability(
                token,
                offerId,
                regaliaProductId);

        /*
         * HDFC Millennia does not match the CardProduct,
         * but its bank matches.
         *
         * Current V1 contract:
         * bank match OR card-product match = eligible.
         */
        checkEligibility(
                token,
                offerId,
                hdfcMillenniaCardId,
                new BigDecimal("1000"))
                .andExpect(jsonPath("$.data.eligible")
                        .value(true));

        /*
         * ICICI matches neither the bank nor the
         * CardProduct.
         */
        checkEligibility(
                token,
                offerId,
                iciciCardId,
                new BigDecimal("1000"))
                .andExpect(jsonPath("$.data.eligible")
                        .value(false));
    }

    @Test
    void combinedBankAndCardProductShouldAcceptCardProductMatch()
            throws Exception {

        String token = registerAndLogin(
                "Combined Card Match User",
                uniqueEmail("combinedcard"));

        long hdfcBankId = createBank(
                token,
                "HDFC Bank");

        long regaliaProductId = createCardProduct(
                hdfcBankId,
                "HDFC Regalia",
                "CREDIT",
                true);

        long regaliaCardId = createCard(
                token,
                "HDFC Regalia",
                hdfcBankId,
                regaliaProductId);

        long offerId = createOffer(
                token,
                "Regalia Specific Offer",
                new BigDecimal("20"),
                null);

        addBankApplicability(
                token,
                offerId,
                hdfcBankId);

        addCardApplicability(
                token,
                offerId,
                regaliaProductId);

        checkEligibility(
                token,
                offerId,
                regaliaCardId,
                new BigDecimal("1000"))
                .andExpect(jsonPath("$.data.eligible")
                        .value(true));
    }

    private org.springframework.test.web.servlet.ResultActions checkEligibility(
            String token,
            long offerId,
            long cardId,
            BigDecimal transactionAmount)
            throws Exception {

        String json = """
                {
                    "cardId": %d,
                    "transactionAmount": %s
                }
                """.formatted(
                cardId,
                transactionAmount.toPlainString());

        return mockMvc.perform(
                post("/api/offers/" + offerId + "/eligibility")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    private long createOfferFromJson(
            String token,
            String json)
            throws Exception {

        return extractId(
                mockMvc.perform(
                        post("/api/offers")
                                .header("Authorization", auth(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                        .andExpect(status().isCreated())
                        .andReturn());
    }
}