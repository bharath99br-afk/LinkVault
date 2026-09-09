package com.linkvault.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class DashboardIntegrationTest extends IntegrationTestBase {

    @Test
    void dashboardShouldReturnCorrectUserOwnedCounts()
            throws Exception {

        String token =
                registerAndLogin(
                        "Dashboard User",
                        uniqueEmail("dashboard"));

        long bankId =
                createBank(token, "Dashboard Bank");

        createCard(
                token,
                "Dashboard Card",
                bankId);

        long globalMerchantId =
                createGlobalMerchant(
                        token,
                        "Dashboard Merchant");

        long merchantId =
                createMerchant(
                        token,
                        "Dashboard Merchant",
                        globalMerchantId);

        long productId =
                createProduct(
                        token,
                        "Dashboard Product",
                        merchantId);

        createLink(
                token,
                "Dashboard Link",
                merchantId,
                productId);

        createOffer(
                token,
                "Dashboard Active Offer",
                new BigDecimal("10"),
                globalMerchantId);

        mockMvc.perform(
                get("/api/dashboard/summary")
                        .header("Authorization", auth(token)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data.userName")
                                .value("Dashboard User"))
                .andExpect(
                        jsonPath("$.data.totalLinks")
                                .value(1))
                .andExpect(
                        jsonPath("$.data.totalProducts")
                                .value(1))
                .andExpect(
                        jsonPath("$.data.totalMerchants")
                                .value(1))
                .andExpect(
                        jsonPath("$.data.totalCards")
                                .value(1))
                .andExpect(
                        jsonPath("$.data.activeOffers")
                                .value(1));
    }
}