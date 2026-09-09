package com.linkvault.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;

class CommerceFlowIntegrationTest extends IntegrationTestBase {

    @Test
    void completeCommerceRelationshipShouldWork()
            throws Exception {

        String token =
                registerAndLogin(
                        "Commerce User",
                        uniqueEmail("commerce"));

        long globalMerchantId =
                createGlobalMerchant(token, "Amazon");

        long merchantId =
                createMerchant(
                        token,
                        "Amazon",
                        globalMerchantId);

        long productId =
                createProduct(
                        token,
                        "Laptop",
                        merchantId);

        long linkId =
                createLink(
                        token,
                        "Laptop Purchase",
                        merchantId,
                        productId);

        mockMvc.perform(
                get("/api/merchants/" + merchantId)
                        .header("Authorization", auth(token)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data.globalMerchantId")
                                .value(globalMerchantId));

        mockMvc.perform(
                get("/api/products/" + productId)
                        .header("Authorization", auth(token)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data.merchantId")
                                .value(merchantId))
                .andExpect(
                        jsonPath("$.data.globalMerchantId")
                                .value(globalMerchantId));

        // Link has no GET-by-ID endpoint, therefore verify it through
        // the user's link collection.
        mockMvc.perform(
                get("/api/links")
                        .header("Authorization", auth(token)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data.content[0].id")
                                .value(linkId))
                .andExpect(
                        jsonPath("$.data.content[0].merchantId")
                                .value(merchantId))
                .andExpect(
                        jsonPath("$.data.content[0].productId")
                                .value(productId));
    }

    @Test
    void merchantNameShouldAutomaticallyMatchGlobalMerchant()
            throws Exception {

        String token =
                registerAndLogin(
                        "Matcher User",
                        uniqueEmail("matcher"));

        long globalMerchantId =
                createGlobalMerchant(token, "Flipkart");

        long merchantId =
                createMerchant(
                        token,
                        "Flipkart",
                        null);

        mockMvc.perform(
                get("/api/merchants/" + merchantId)
                        .header("Authorization", auth(token)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data.globalMerchantId")
                                .value(globalMerchantId));
    }
}