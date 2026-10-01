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

                String token = registerAndLogin(
                                "Best Deal User",
                                uniqueEmail("best-deal"));

                long bankId = createBank(token, "HDFC");

                createCard(
                                token,
                                "HDFC Card",
                                bankId);

                long amazonGlobalMerchant = createGlobalMerchant(
                                token,
                                "Amazon");

                long amazonMerchant = createMerchant(
                                token,
                                "Amazon",
                                amazonGlobalMerchant);

                long productId = createProduct(
                                token,
                                "Laptop",
                                amazonMerchant);

                long linkId = createLink(
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

                String token = registerAndLogin(
                                "Merchant Filter User",
                                uniqueEmail("merchant-filter"));

                long bankId = createBank(token, "ICICI");

                createCard(
                                token,
                                "ICICI Card",
                                bankId);

                long amazonGlobal = createGlobalMerchant(token, "Amazon");

                long flipkartGlobal = createGlobalMerchant(token, "Flipkart");

                long amazonMerchant = createMerchant(
                                token,
                                "Amazon",
                                amazonGlobal);

                long productId = createProduct(
                                token,
                                "Headphones",
                                amazonMerchant);

                long linkId = createLink(
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

                String token = registerAndLogin(
                                "Amount Only User",
                                uniqueEmail("amount-only"));

                long bankId = createBank(token, "Axis");

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

                String ownerToken = registerAndLogin(
                                "Owner",
                                uniqueEmail("owner"));

                String attackerToken = registerAndLogin(
                                "Attacker",
                                uniqueEmail("attacker"));

                long linkId = createLink(
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

                String token = registerAndLogin(
                                "No Deal User",
                                uniqueEmail("no-deal"));

                long bankId = createBank(token, "Bank A");

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

        @Test
        void bankSpecificOfferShouldApplyToMatchingBankCard()
                        throws Exception {

                String token = registerAndLogin(
                                "Bank Match User",
                                uniqueEmail("bank-match"));

                long hdfcBankId = createBank(token, "HDFC");

                long iciciBankId = createBank(token, "ICICI");

                createCard(
                                token,
                                "HDFC Card",
                                hdfcBankId);

                createCard(
                                token,
                                "ICICI Card",
                                iciciBankId);

                long offerId = createOffer(
                                token,
                                "HDFC 20 Percent",
                                new BigDecimal("20"),
                                null);

                addBankApplicability(
                                token,
                                offerId,
                                hdfcBankId);

                String request = """
                                {
                                    "transactionAmount": 10000
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
                                                                .value("HDFC 20 Percent"))
                                .andExpect(
                                                jsonPath("$.data.bestDeal.finalAmount")
                                                                .value(8000.00));
        }

        @Test
        void bankSpecificOfferShouldExcludeNonMatchingBankCard()
                        throws Exception {

                String token = registerAndLogin(
                                "Bank Mismatch User",
                                uniqueEmail("bank-mismatch"));

                long hdfcBankId = createBank(token, "HDFC");

                long iciciBankId = createBank(token, "ICICI");

                createCard(
                                token,
                                "ICICI Card",
                                iciciBankId);

                long offerId = createOffer(
                                token,
                                "HDFC 30 Percent",
                                new BigDecimal("30"),
                                null);

                addBankApplicability(
                                token,
                                offerId,
                                hdfcBankId);

                String request = """
                                {
                                    "transactionAmount": 10000
                                }
                                """;

                mockMvc.perform(
                                post("/api/deals/best")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(request))
                                .andExpect(status().isOk())
                                .andExpect(
                                                jsonPath("$.data.bestDeal")
                                                                .doesNotExist())
                                .andExpect(
                                                jsonPath("$.data.alternatives.size()")
                                                                .value(0));
        }

        @Test
        void minimumTransactionAmountShouldBeEnforcedByBestDeal()
                        throws Exception {

                String token = registerAndLogin(
                                "Minimum Amount User",
                                uniqueEmail("minimum-amount"));

                long bankId = createBank(token, "Axis");

                createCard(
                                token,
                                "Axis Card",
                                bankId);

                String offerJson = """
                                {
                                    "title": "Minimum Spend Offer",
                                    "description": "Minimum spend required",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 20,
                                    "minTransactionAmount": 5000,
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
                                .andExpect(status().isCreated());

                String request = """
                                {
                                    "transactionAmount": 4000
                                }
                                """;

                mockMvc.perform(
                                post("/api/deals/best")
                                                .header("Authorization", auth(token))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(request))
                                .andExpect(status().isOk())
                                .andExpect(
                                                jsonPath("$.data.bestDeal")
                                                                .doesNotExist())
                                .andExpect(
                                                jsonPath("$.data.alternatives.size()")
                                                                .value(0));
        }

        @Test
        void cardProductSpecificOfferShouldApplyToMatchingCardProduct()
                        throws Exception {

                String token = registerAndLogin(
                                "Card Product User",
                                uniqueEmail("card-product"));

                long bankId = createBank(token, "HDFC");

                long matchingCardProductId = createCardProduct(
                                bankId,
                                "HDFC Regalia",
                                "CREDIT",
                                true);

                long otherCardProductId = createCardProduct(
                                bankId,
                                "HDFC Millennia",
                                "CREDIT",
                                true);

                createCard(
                                token,
                                "Regalia",
                                bankId,
                                matchingCardProductId);

                createCard(
                                token,
                                "Millennia",
                                bankId,
                                otherCardProductId);

                long offerId = createOffer(
                                token,
                                "Regalia 25 Percent",
                                new BigDecimal("25"),
                                null);

                addCardApplicability(
                                token,
                                offerId,
                                matchingCardProductId);

                String request = """
                                {
                                    "transactionAmount": 10000
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
                                                                .value("Regalia 25 Percent"))
                                .andExpect(
                                                jsonPath("$.data.bestDeal.finalAmount")
                                                                .value(7500.00));
        }

        @Test
        void flatDiscountShouldBeCalculatedCorrectly()
                        throws Exception {

                String token = registerAndLogin(
                                "Flat Discount User",
                                uniqueEmail("flat-discount"));

                long bankId = createBank(token, "SBI");

                createCard(
                                token,
                                "SBI Card",
                                bankId);

                String offerJson = """
                                {
                                    "title": "Flat 1000 Off",
                                    "description": "Flat discount offer",
                                    "discountType": "FLAT",
                                    "discountValue": 1000,
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
                                .andExpect(status().isCreated());

                String request = """
                                {
                                    "transactionAmount": 10000
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
                                                                .value("Flat 1000 Off"))
                                .andExpect(
                                                jsonPath("$.data.bestDeal.discountAmount")
                                                                .value(1000.00))
                                .andExpect(
                                                jsonPath("$.data.bestDeal.finalAmount")
                                                                .value(9000.00));
        }

        @Test
        void percentageDiscountShouldRespectMaximumDiscountCap()
                        throws Exception {

                String token = registerAndLogin(
                                "Max Discount User",
                                uniqueEmail("max-discount"));

                long bankId = createBank(token, "Kotak");

                createCard(
                                token,
                                "Kotak Card",
                                bankId);

                String offerJson = """
                                {
                                    "title": "20 Percent Capped",
                                    "description": "Percentage discount with cap",
                                    "discountType": "PERCENTAGE",
                                    "discountValue": 20,
                                    "maxDiscount": 1000,
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
                                .andExpect(status().isCreated());

                String request = """
                                {
                                    "transactionAmount": 10000
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
                                                                .value("20 Percent Capped"))
                                .andExpect(
                                                jsonPath("$.data.bestDeal.discountAmount")
                                                                .value(1000.00))
                                .andExpect(
                                                jsonPath("$.data.bestDeal.finalAmount")
                                                                .value(9000.00));
        }

        @Test
        void discountShouldNotExceedTransactionAmount()
                        throws Exception {

                String token = registerAndLogin(
                                "Discount Limit User",
                                uniqueEmail("discount-limit"));

                long bankId = createBank(token, "Axis");

                createCard(
                                token,
                                "Axis Card",
                                bankId);

                String offerJson = """
                                {
                                    "title": "Flat 1000 Off",
                                    "description": "Discount greater than transaction",
                                    "discountType": "FLAT",
                                    "discountValue": 1000,
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
                                .andExpect(status().isCreated());

                String request = """
                                {
                                    "transactionAmount": 500
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
                                                                .value("Flat 1000 Off"))
                                .andExpect(
                                                jsonPath("$.data.bestDeal.discountAmount")
                                                                .value(500.00))
                                .andExpect(
                                                jsonPath("$.data.bestDeal.finalAmount")
                                                                .value(0.00));
        }

        @Test
        void bestDealShouldSelectLowestFinalAmount()
                        throws Exception {

                String token = registerAndLogin(
                                "Ranking User",
                                uniqueEmail("ranking"));

                long bankId = createBank(token, "HDFC");

                createCard(
                                token,
                                "HDFC Card",
                                bankId);

                createOffer(
                                token,
                                "10 Percent Offer",
                                new BigDecimal("10"),
                                null);

                createOffer(
                                token,
                                "20 Percent Offer",
                                new BigDecimal("20"),
                                null);

                String request = """
                                {
                                    "transactionAmount": 10000
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
                                                                .value("20 Percent Offer"))
                                .andExpect(
                                                jsonPath("$.data.bestDeal.discountAmount")
                                                                .value(2000.00))
                                .andExpect(
                                                jsonPath("$.data.bestDeal.finalAmount")
                                                                .value(8000.00))
                                .andExpect(
                                                jsonPath("$.data.alternatives[0].offerTitle")
                                                                .value("10 Percent Offer"))
                                .andExpect(
                                                jsonPath("$.data.alternatives[0].finalAmount")
                                                                .value(9000.00));
        }

        @Test
        void multipleEligibleCardsShouldProduceSeparateDealOptions()
                        throws Exception {

                String token = registerAndLogin(
                                "Multiple Cards User",
                                uniqueEmail("multiple-cards"));

                long hdfcBankId = createBank(token, "HDFC");

                long iciciBankId = createBank(token, "ICICI");

                createCard(
                                token,
                                "HDFC Card",
                                hdfcBankId);

                createCard(
                                token,
                                "ICICI Card",
                                iciciBankId);

                createOffer(
                                token,
                                "General 10 Percent",
                                new BigDecimal("10"),
                                null);

                String request = """
                                {
                                    "transactionAmount": 10000
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
                                                                .value("General 10 Percent"))
                                .andExpect(
                                                jsonPath("$.data.bestDeal.finalAmount")
                                                                .value(9000.00))
                                .andExpect(
                                                jsonPath("$.data.alternatives.size()")
                                                                .value(1))
                                .andExpect(
                                                jsonPath("$.data.alternatives[0].offerTitle")
                                                                .value("General 10 Percent"));
        }

        @Test
        void bestDealShouldRankEligibleOfferCardCombinations()
                        throws Exception {

                String token = registerAndLogin(
                                "Combination Ranking User",
                                uniqueEmail("combination-ranking"));

                long hdfcBankId = createBank(token, "HDFC");

                long iciciBankId = createBank(token, "ICICI");

                createCard(
                                token,
                                "HDFC Card",
                                hdfcBankId);

                createCard(
                                token,
                                "ICICI Card",
                                iciciBankId);

                createOffer(
                                token,
                                "General 10 Percent",
                                new BigDecimal("10"),
                                null);

                long hdfcOfferId = createOffer(
                                token,
                                "HDFC 20 Percent",
                                new BigDecimal("20"),
                                null);

                addBankApplicability(
                                token,
                                hdfcOfferId,
                                hdfcBankId);

                String request = """
                                {
                                    "transactionAmount": 10000
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
                                                                .value("HDFC 20 Percent"))
                                .andExpect(
                                                jsonPath("$.data.bestDeal.cardName")
                                                                .value("HDFC Card"))
                                .andExpect(
                                                jsonPath("$.data.bestDeal.finalAmount")
                                                                .value(8000.00))
                                .andExpect(
                                                jsonPath("$.data.alternatives.size()")
                                                                .value(2));
        }
}