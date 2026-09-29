package com.linkvault.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import static org.assertj.core.api.Assertions.assertThat;
import tools.jackson.databind.JsonNode;

class OfferIntegrationTest extends IntegrationTestBase {

        @Test
        void offerShouldBeEligibleForMatchingBank()
                        throws Exception {

                String token = registerAndLogin(
                                "Offer User",
                                uniqueEmail("offer-bank"));

                long bankId = createBank(token, "HDFC Bank");

                long cardId = createCard(
                                token,
                                "HDFC Regalia",
                                bankId);

                long offerId = createOffer(
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

                String token = registerAndLogin(
                                "Calculation User",
                                uniqueEmail("calculation"));

                long bankId = createBank(token, "ICICI Bank");

                long cardId = createCard(
                                token,
                                "ICICI Amazon Card",
                                bankId);

                long offerId = createOffer(
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

                String token = registerAndLogin(
                                "Minimum User",
                                uniqueEmail("minimum"));

                long bankId = createBank(token, "Axis Bank");

                long cardId = createCard(
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

                String token = registerAndLogin(
                                "Duplicate Applicability",
                                uniqueEmail("applicability"));

                long bankId = createBank(token, "SBI");

                long offerId = createOffer(
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

        @Test
        void offerShouldCreateAndReturnSourceUrl() throws Exception {

                String token = registerAndLogin(
                                "Source URL User",
                                uniqueEmail("offer-source-url"));

                String sourceUrl = "https://www.example.com/offers/10-percent-discount";

                String offerJson = """
                                {
                                    "title": "Source URL Offer",
                                    "description": "Offer with source URL",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "startDate": "%s",
                                    "endDate": "%s",
                                    "sourceUrl": "%s"
                                }
                                """.formatted(
                                java.time.LocalDate.now().minusDays(1),
                                java.time.LocalDate.now().plusDays(30),
                                sourceUrl);

                var result = mockMvc.perform(
                                post("/api/offers")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(offerJson))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.sourceUrl").value(sourceUrl))
                                .andReturn();

                long offerId = extractId(result);

                mockMvc.perform(
                                get("/api/offers/" + offerId)
                                                .header("Authorization", auth(token)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.id").value(offerId))
                                .andExpect(jsonPath("$.data.sourceUrl").value(sourceUrl));
        }

        @Test
        void offerShouldUpdateSourceUrl() throws Exception {

                String token = registerAndLogin(
                                "Update Source URL User",
                                uniqueEmail("offer-update-source-url"));

                String originalUrl = "https://www.example.com/offers/original";

                String createJson = """
                                {
                                    "title": "Update Source URL Offer",
                                    "description": "Source URL update test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "startDate": "%s",
                                    "endDate": "%s",
                                    "sourceUrl": "%s"
                                }
                                """.formatted(
                                java.time.LocalDate.now().minusDays(1),
                                java.time.LocalDate.now().plusDays(30),
                                originalUrl);

                var result = mockMvc.perform(
                                post("/api/offers")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(createJson))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.sourceUrl").value(originalUrl))
                                .andReturn();

                long offerId = extractId(result);

                String updatedUrl = "https://www.example.com/offers/updated";

                String updateJson = """
                                {
                                    "title": "Updated Source URL Offer",
                                    "description": "Updated source URL",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 15,
                                    "startDate": "%s",
                                    "endDate": "%s",
                                    "sourceUrl": "%s"
                                }
                                """.formatted(
                                java.time.LocalDate.now().minusDays(1),
                                java.time.LocalDate.now().plusDays(45),
                                updatedUrl);

                mockMvc.perform(
                                put("/api/offers/" + offerId)
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(updateJson))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.sourceUrl").value(updatedUrl));

                mockMvc.perform(
                                get("/api/offers/" + offerId)
                                                .header("Authorization", auth(token)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.sourceUrl").value(updatedUrl));
        }

        @Test
        void offerShouldAllowMissingSourceUrl() throws Exception {

                String token = registerAndLogin(
                                "Optional Source URL User",
                                uniqueEmail("offer-no-source-url"));

                String offerJson = """
                                {
                                    "title": "Offer Without Source URL",
                                    "description": "Source URL is optional",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 5,
                                    "startDate": "%s",
                                    "endDate": "%s"
                                }
                                """.formatted(
                                java.time.LocalDate.now().minusDays(1),
                                java.time.LocalDate.now().plusDays(30));

                mockMvc.perform(
                                post("/api/offers")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(offerJson))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.sourceUrl").doesNotExist());
        }

        @Test
        void offerShouldBeCreatedThroughIngestion() throws Exception {

                String token = registerAndLogin(
                                "Ingestion User",
                                uniqueEmail("ingestion"));

                long merchantId = createGlobalMerchant(
                                token,
                                "Amazon");

                String json = """
                                {
                                    "title": "Amazon discount offer",
                                    "description": "Integration ingestion test offer",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "maxDiscount": 1000,
                                    "minTransactionAmount": 500,
                                    "startDate": "%s",
                                    "endDate": "%s",
                                    "globalMerchantId": %d,
                                    "sourceUrl": "https://amazon.example.com/offer"
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30),
                                merchantId);

                MvcResult result = mockMvc.perform(
                                post("/api/offers/ingest")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isCreated())
                                .andReturn();

                JsonNode root = objectMapper.readTree(
                                result.getResponse().getContentAsString());

                JsonNode offer = root.path("data");

                assertThat(offer.path("title").asText())
                                .isEqualTo("Amazon discount offer");

                assertThat(offer.path("discountValue").asDouble())
                                .isEqualTo(10.0);

                assertThat(offer.path("globalMerchantId").asLong())
                                .isEqualTo(merchantId);

                assertThat(offer.path("sourceUrl").asText())
                                .isEqualTo("https://amazon.example.com/offer");
        }

        @Test
        void offerShouldNormalizeIngestedTextFields() throws Exception {

                String token = registerAndLogin(
                                "Ingestion User",
                                uniqueEmail("ingestion"));

                long merchantId = createGlobalMerchant(
                                token,
                                "Amazon");

                String json = """
                                {
                                    "title": "   Amazon discount offer   ",
                                    "description": "   Integration ingestion test offer   ",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "maxDiscount": 1000,
                                    "minTransactionAmount": 500,
                                    "startDate": "%s",
                                    "endDate": "%s",
                                    "globalMerchantId": %d,
                                    "sourceUrl": "   https://amazon.example.com/offer   "
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30),
                                merchantId);

                mockMvc.perform(post("/api/offers/ingest")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.title")
                                                .value("Amazon discount offer"))
                                .andExpect(jsonPath("$.data.description")
                                                .value("Integration ingestion test offer"))
                                .andExpect(jsonPath("$.data.sourceUrl")
                                                .value("https://amazon.example.com/offer"));
        }

        @Test
        void duplicateIngestionShouldReturnExistingOffer() throws Exception {

                String token = registerAndLogin(
                                "Duplicate Ingestion User",
                                uniqueEmail("duplicate-ingestion"));

                long merchantId = createGlobalMerchant(
                                token,
                                "Amazon");

                String json = """
                                {
                                    "title": "Amazon duplicate test offer",
                                    "description": "Duplicate ingestion test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "maxDiscount": 1000,
                                    "minTransactionAmount": 500,
                                    "startDate": "%s",
                                    "endDate": "%s",
                                    "globalMerchantId": %d,
                                    "sourceUrl": "https://amazon.example.com/offer"
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30),
                                merchantId);

                MvcResult firstResponse = mockMvc.perform(post("/api/offers/ingest")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.title")
                                                .value("Amazon duplicate test offer"))
                                .andReturn();

                long firstOfferId = extractId(firstResponse);

                MvcResult secondResponse = mockMvc.perform(post("/api/offers/ingest")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.title")
                                                .value("Amazon duplicate test offer"))
                                .andReturn();

                long secondOfferId = extractId(secondResponse);

                assertThat(secondOfferId).isEqualTo(firstOfferId);
        }

        @Test
        void differentMinimumTransactionAmountShouldCreateNewOffer() throws Exception {

                String token = registerAndLogin(
                                "Different Offer User",
                                uniqueEmail("different-offer"));

                long merchantId = createGlobalMerchant(
                                token,
                                "Amazon");

                String firstJson = """
                                {
                                    "title": "Amazon threshold offer",
                                    "description": "First threshold",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "maxDiscount": 1000,
                                    "minTransactionAmount": 500,
                                    "startDate": "%s",
                                    "endDate": "%s",
                                    "globalMerchantId": %d,
                                    "sourceUrl": "https://amazon.example.com/offer"
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30),
                                merchantId);

                String secondJson = """
                                {
                                    "title": "Amazon threshold offer",
                                    "description": "Second threshold",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "maxDiscount": 1000,
                                    "minTransactionAmount": 1000,
                                    "startDate": "%s",
                                    "endDate": "%s",
                                    "globalMerchantId": %d,
                                    "sourceUrl": "https://amazon.example.com/offer"
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30),
                                merchantId);

                MvcResult firstResponse = mockMvc.perform(post("/api/offers/ingest")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(firstJson))
                                .andExpect(status().isCreated())
                                .andReturn();

                MvcResult secondResponse = mockMvc.perform(post("/api/offers/ingest")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(secondJson))
                                .andExpect(status().isCreated())
                                .andReturn();

                long firstOfferId = extractId(firstResponse);
                long secondOfferId = extractId(secondResponse);

                assertThat(secondOfferId).isNotEqualTo(firstOfferId);
        }

        @Test
        void differentSourceUrlShouldStillReturnExistingOffer() throws Exception {

                String token = registerAndLogin(
                                "Different Source User",
                                uniqueEmail("different-source"));

                long merchantId = createGlobalMerchant(
                                token,
                                "Amazon");

                String firstJson = """
                                {
                                    "title": "Amazon source test offer",
                                    "description": "Same canonical offer",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 15,
                                    "maxDiscount": 1500,
                                    "minTransactionAmount": 1000,
                                    "startDate": "%s",
                                    "endDate": "%s",
                                    "globalMerchantId": %d,
                                    "sourceUrl": "https://source-a.example.com/offer"
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30),
                                merchantId);

                String secondJson = """
                                {
                                    "title": "Amazon source test offer",
                                    "description": "Same canonical offer",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 15,
                                    "maxDiscount": 1500,
                                    "minTransactionAmount": 1000,
                                    "startDate": "%s",
                                    "endDate": "%s",
                                    "globalMerchantId": %d,
                                    "sourceUrl": "https://source-b.example.com/offer"
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30),
                                merchantId);

                MvcResult firstResponse = mockMvc.perform(post("/api/offers/ingest")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(firstJson))
                                .andExpect(status().isCreated())
                                .andReturn();

                MvcResult secondResponse = mockMvc.perform(post("/api/offers/ingest")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(secondJson))
                                .andExpect(status().isCreated())
                                .andReturn();

                long firstOfferId = extractId(firstResponse);
                long secondOfferId = extractId(secondResponse);

                assertThat(secondOfferId).isEqualTo(firstOfferId);
        }

        @Test
        void duplicateIngestionShouldRefreshDescriptionAndSourceUrl() throws Exception {

                String token = registerAndLogin(
                                "Refresh Ingestion User",
                                uniqueEmail("refresh-ingestion"));

                long merchantId = createGlobalMerchant(
                                token,
                                "Amazon");

                String firstJson = """
                                {
                                    "title": "Amazon refresh test offer",
                                    "description": "Original description",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "maxDiscount": 1000,
                                    "minTransactionAmount": 500,
                                    "startDate": "%s",
                                    "endDate": "%s",
                                    "globalMerchantId": %d,
                                    "sourceUrl": "https://source-a.example.com/offer"
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30),
                                merchantId);

                MvcResult firstResponse = mockMvc.perform(
                                post("/api/offers/ingest")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(firstJson))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.description")
                                                .value("Original description"))
                                .andExpect(jsonPath("$.data.sourceUrl")
                                                .value("https://source-a.example.com/offer"))
                                .andReturn();

                long offerId = extractId(firstResponse);

                String secondJson = """
                                {
                                    "title": "Amazon refresh test offer",
                                    "description": "Updated description",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "maxDiscount": 1000,
                                    "minTransactionAmount": 500,
                                    "startDate": "%s",
                                    "endDate": "%s",
                                    "globalMerchantId": %d,
                                    "sourceUrl": "https://source-b.example.com/offer"
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30),
                                merchantId);

                MvcResult secondResponse = mockMvc.perform(
                                post("/api/offers/ingest")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(secondJson))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.description")
                                                .value("Updated description"))
                                .andExpect(jsonPath("$.data.sourceUrl")
                                                .value("https://source-b.example.com/offer"))
                                .andReturn();

                long secondOfferId = extractId(secondResponse);

                assertThat(secondOfferId).isEqualTo(offerId);
        }

        @Test
        void ingestionShouldRejectBlankTitle() throws Exception {

                String token = registerAndLogin(
                                "Invalid Title User",
                                uniqueEmail("invalid-title"));

                String json = """
                                {
                                    "title": "   ",
                                    "description": "Invalid title test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "startDate": "%s",
                                    "endDate": "%s"
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30));

                mockMvc.perform(
                                post("/api/offers/ingest")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void ingestionShouldRejectInvalidDiscountValue() throws Exception {

                String token = registerAndLogin(
                                "Invalid Discount User",
                                uniqueEmail("invalid-discount"));

                String json = """
                                {
                                    "title": "Invalid Discount Offer",
                                    "description": "Invalid discount test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 0,
                                    "startDate": "%s",
                                    "endDate": "%s"
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30));

                mockMvc.perform(
                                post("/api/offers/ingest")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void ingestionShouldRejectInvalidMaxDiscount() throws Exception {

                String token = registerAndLogin(
                                "Invalid Max Discount User",
                                uniqueEmail("invalid-max-discount"));

                String json = """
                                {
                                    "title": "Invalid Max Discount Offer",
                                    "description": "Invalid max discount test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "maxDiscount": 0,
                                    "startDate": "%s",
                                    "endDate": "%s"
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30));

                mockMvc.perform(
                                post("/api/offers/ingest")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void ingestionShouldRejectInvalidMinimumTransactionAmount() throws Exception {

                String token = registerAndLogin(
                                "Invalid Minimum User",
                                uniqueEmail("invalid-minimum"));

                String json = """
                                {
                                    "title": "Invalid Minimum Offer",
                                    "description": "Invalid minimum transaction test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "minTransactionAmount": 0,
                                    "startDate": "%s",
                                    "endDate": "%s"
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30));

                mockMvc.perform(
                                post("/api/offers/ingest")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void ingestionShouldRejectEndDateBeforeStartDate() throws Exception {

                String token = registerAndLogin(
                                "Invalid Date User",
                                uniqueEmail("invalid-date"));

                String json = """
                                {
                                    "title": "Invalid Date Offer",
                                    "description": "Invalid date range test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "startDate": "%s",
                                    "endDate": "%s"
                                }
                                """.formatted(
                                LocalDate.now().plusDays(30),
                                LocalDate.now());

                mockMvc.perform(
                                post("/api/offers/ingest")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void ingestionShouldRejectOversizedSourceUrl() throws Exception {

                String token = registerAndLogin(
                                "Invalid Source URL User",
                                uniqueEmail("invalid-source-url"));

                String oversizedUrl = "https://example.com/" + "a".repeat(2048);

                String json = """
                                {
                                    "title": "Oversized Source URL Offer",
                                    "description": "Oversized source URL test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "startDate": "%s",
                                    "endDate": "%s",
                                    "sourceUrl": "%s"
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30),
                                oversizedUrl);

                mockMvc.perform(
                                post("/api/offers/ingest")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void offerShouldBeUpcomingBeforeStartDate() throws Exception {

                String token = registerAndLogin(
                                "Upcoming Offer User",
                                uniqueEmail("upcoming-offer"));

                String json = """
                                {
                                    "title": "Upcoming Lifecycle Offer",
                                    "description": "Upcoming offer test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "startDate": "%s",
                                    "endDate": "%s"
                                }
                                """.formatted(
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(30));

                mockMvc.perform(
                                post("/api/offers")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.status")
                                                .value("UPCOMING"));
        }

        @Test
        void offerShouldBeActiveDuringOfferPeriod() throws Exception {

                String token = registerAndLogin(
                                "Active Offer User",
                                uniqueEmail("active-offer"));

                String json = """
                                {
                                    "title": "Active Lifecycle Offer",
                                    "description": "Active offer test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "startDate": "%s",
                                    "endDate": "%s"
                                }
                                """.formatted(
                                LocalDate.now(),
                                LocalDate.now().plusDays(30));

                mockMvc.perform(
                                post("/api/offers")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.status")
                                                .value("ACTIVE"));
        }

        @Test
        void offerShouldBeExpiredAfterEndDate() throws Exception {

                String token = registerAndLogin(
                                "Expired Offer User",
                                uniqueEmail("expired-offer"));

                String json = """
                                {
                                    "title": "Expired Lifecycle Offer",
                                    "description": "Expired offer test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "startDate": "%s",
                                    "endDate": "%s"
                                }
                                """.formatted(
                                LocalDate.now().minusDays(30),
                                LocalDate.now().minusDays(1));

                mockMvc.perform(
                                post("/api/offers")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.status")
                                                .value("EXPIRED"));
        }

        @Test
        void offerDiscoveryShouldFilterActiveOffers() throws Exception {

                String token = registerAndLogin(
                                "Active Discovery User",
                                uniqueEmail("active-discovery"));

                String activeJson = """
                                {
                                    "title": "Active Discovery Offer",
                                    "description": "Active discovery test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "startDate": "%s",
                                    "endDate": "%s"
                                }
                                """.formatted(
                                LocalDate.now().minusDays(1),
                                LocalDate.now().plusDays(30));

                mockMvc.perform(
                                post("/api/offers")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(activeJson))
                                .andExpect(status().isCreated());

                mockMvc.perform(
                                get("/api/offers")
                                                .header("Authorization", auth(token))
                                                .param("status", "ACTIVE"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.content").isArray())
                                .andExpect(jsonPath("$.data.content[?(@.title == 'Active Discovery Offer')]")
                                                .exists());
        }

        @Test
        void offerDiscoveryShouldFilterUpcomingOffers() throws Exception {

                String token = registerAndLogin(
                                "Upcoming Discovery User",
                                uniqueEmail("upcoming-discovery"));

                String upcomingJson = """
                                {
                                    "title": "Upcoming Discovery Offer",
                                    "description": "Upcoming discovery test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "startDate": "%s",
                                    "endDate": "%s"
                                }
                                """.formatted(
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(30));

                mockMvc.perform(
                                post("/api/offers")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(upcomingJson))
                                .andExpect(status().isCreated());

                mockMvc.perform(
                                get("/api/offers")
                                                .header("Authorization", auth(token))
                                                .param("status", "UPCOMING"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.content").isArray())
                                .andExpect(jsonPath("$.data.content[?(@.title == 'Upcoming Discovery Offer')]")
                                                .exists());
        }

        @Test
        void offerDiscoveryShouldFilterExpiredOffers() throws Exception {

                String token = registerAndLogin(
                                "Expired Discovery User",
                                uniqueEmail("expired-discovery"));

                String expiredJson = """
                                {
                                    "title": "Expired Discovery Offer",
                                    "description": "Expired discovery test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "startDate": "%s",
                                    "endDate": "%s"
                                }
                                """.formatted(
                                LocalDate.now().minusDays(30),
                                LocalDate.now().minusDays(1));

                mockMvc.perform(
                                post("/api/offers")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(expiredJson))
                                .andExpect(status().isCreated());

                mockMvc.perform(
                                get("/api/offers")
                                                .header("Authorization", auth(token))
                                                .param("status", "EXPIRED"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.content").isArray())
                                .andExpect(jsonPath("$.data.content[?(@.title == 'Expired Discovery Offer')]")
                                                .exists());
        }

        @Test
        void offerDiscoveryShouldFilterByTitleAndStatus() throws Exception {

                String token = registerAndLogin(
                                "Combined Discovery User",
                                uniqueEmail("combined-discovery"));

                String activeJson = """
                                {
                                    "title": "Amazon Active Discovery Offer",
                                    "description": "Active Amazon discovery test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 10,
                                    "startDate": "%s",
                                    "endDate": "%s"
                                }
                                """.formatted(
                                LocalDate.now().minusDays(1),
                                LocalDate.now().plusDays(30));

                String upcomingJson = """
                                {
                                    "title": "Amazon Upcoming Discovery Offer",
                                    "description": "Upcoming Amazon discovery test",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 15,
                                    "startDate": "%s",
                                    "endDate": "%s"
                                }
                                """.formatted(
                                LocalDate.now().plusDays(1),
                                LocalDate.now().plusDays(30));

                mockMvc.perform(
                                post("/api/offers")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(activeJson))
                                .andExpect(status().isCreated());

                mockMvc.perform(
                                post("/api/offers")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(upcomingJson))
                                .andExpect(status().isCreated());

                mockMvc.perform(
                                get("/api/offers")
                                                .header("Authorization", auth(token))
                                                .param("title", "Amazon")
                                                .param("status", "ACTIVE"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.content").isArray())
                                .andExpect(jsonPath("$.data.content[?(@.title == 'Amazon Active Discovery Offer')]")
                                                .exists())
                                .andExpect(jsonPath("$.data.content[?(@.title == 'Amazon Upcoming Discovery Offer')]")
                                                .doesNotExist());
        }

        @Test
void offerDiscoveryShouldOrderActiveOffersByStartDateDescending()
                throws Exception {

        String token = registerAndLogin(
                        "Active Ordering User",
                        uniqueEmail("active-ordering"));

        String olderActiveJson = """
                        {
                            "title": "Older Active Offer",
                            "description": "Older active offer",
                            "discountType": "PERCENTAGE",
                            "discountValue": 10,
                            "startDate": "%s",
                            "endDate": "%s"
                        }
                        """.formatted(
                        LocalDate.now().minusDays(10),
                        LocalDate.now().plusDays(10));

        String newerActiveJson = """
                        {
                            "title": "Newer Active Offer",
                            "description": "Newer active offer",
                            "discountType": "PERCENTAGE",
                            "discountValue": 15,
                            "startDate": "%s",
                            "endDate": "%s"
                        }
                        """.formatted(
                        LocalDate.now().minusDays(2),
                        LocalDate.now().plusDays(10));

        mockMvc.perform(
                        post("/api/offers")
                                        .header("Authorization", auth(token))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(olderActiveJson))
                        .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/api/offers")
                                        .header("Authorization", auth(token))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(newerActiveJson))
                        .andExpect(status().isCreated());

        mockMvc.perform(
                        get("/api/offers")
                                        .header("Authorization", auth(token))
                                        .param("status", "ACTIVE"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.content[0].title")
                                        .value("Newer Active Offer"))
                        .andExpect(jsonPath("$.data.content[1].title")
                                        .value("Older Active Offer"));
}

@Test
void offerDiscoveryShouldOrderUpcomingOffersByStartDateAscending()
                throws Exception {

        String token = registerAndLogin(
                        "Upcoming Ordering User",
                        uniqueEmail("upcoming-ordering"));

        String laterUpcomingJson = """
                        {
                            "title": "Later Upcoming Offer",
                            "description": "Later upcoming offer",
                            "discountType": "PERCENTAGE",
                            "discountValue": 10,
                            "startDate": "%s",
                            "endDate": "%s"
                        }
                        """.formatted(
                        LocalDate.now().plusDays(10),
                        LocalDate.now().plusDays(20));

        String earlierUpcomingJson = """
                        {
                            "title": "Earlier Upcoming Offer",
                            "description": "Earlier upcoming offer",
                            "discountType": "PERCENTAGE",
                            "discountValue": 15,
                            "startDate": "%s",
                            "endDate": "%s"
                        }
                        """.formatted(
                        LocalDate.now().plusDays(2),
                        LocalDate.now().plusDays(12));

        mockMvc.perform(
                        post("/api/offers")
                                        .header("Authorization", auth(token))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(laterUpcomingJson))
                        .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/api/offers")
                                        .header("Authorization", auth(token))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(earlierUpcomingJson))
                        .andExpect(status().isCreated());

        mockMvc.perform(
                        get("/api/offers")
                                        .header("Authorization", auth(token))
                                        .param("status", "UPCOMING"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.content[0].title")
                                        .value("Earlier Upcoming Offer"))
                        .andExpect(jsonPath("$.data.content[1].title")
                                        .value("Later Upcoming Offer"));
}

@Test
void offerDiscoveryShouldOrderExpiredOffersByEndDateDescending()
                throws Exception {

        String token = registerAndLogin(
                        "Expired Ordering User",
                        uniqueEmail("expired-ordering"));

        String olderExpiredJson = """
                        {
                            "title": "Older Expired Offer",
                            "description": "Older expired offer",
                            "discountType": "PERCENTAGE",
                            "discountValue": 10,
                            "startDate": "%s",
                            "endDate": "%s"
                        }
                        """.formatted(
                        LocalDate.now().minusDays(20),
                        LocalDate.now().minusDays(10));

        String newerExpiredJson = """
                        {
                            "title": "Newer Expired Offer",
                            "description": "Newer expired offer",
                            "discountType": "PERCENTAGE",
                            "discountValue": 15,
                            "startDate": "%s",
                            "endDate": "%s"
                        }
                        """.formatted(
                        LocalDate.now().minusDays(12),
                        LocalDate.now().minusDays(2));

        mockMvc.perform(
                        post("/api/offers")
                                        .header("Authorization", auth(token))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(olderExpiredJson))
                        .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/api/offers")
                                        .header("Authorization", auth(token))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(newerExpiredJson))
                        .andExpect(status().isCreated());

        mockMvc.perform(
                        get("/api/offers")
                                        .header("Authorization", auth(token))
                                        .param("status", "EXPIRED"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.content[0].title")
                                        .value("Newer Expired Offer"))
                        .andExpect(jsonPath("$.data.content[1].title")
                                        .value("Older Expired Offer"));
}

}