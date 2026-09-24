package com.linkvault.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class SavedOfferIntegrationTest extends IntegrationTestBase {

    @Test
    void userShouldBeAbleToSaveOffer()
            throws Exception {

        String token = registerAndLogin(
                "Saved Offer User",
                uniqueEmail("saved"));

        long offerId = createOffer(
                token,
                "Amazon Cashback",
                new BigDecimal("10"),
                null);

        mockMvc.perform(
                post("/api/saved-offers/" + offerId)
                        .header(
                                "Authorization",
                                auth(token)))
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.data.offerId")
                                .value(offerId))
                .andExpect(
                        jsonPath("$.data.title")
                                .value("Amazon Cashback"));
    }

    @Test
    void sameUserShouldNotSaveOfferTwice()
            throws Exception {

        String token = registerAndLogin(
                "Duplicate Saved Offer",
                uniqueEmail("duplicate-saved"));

        long offerId = createOffer(
                token,
                "Duplicate Test Offer",
                new BigDecimal("5"),
                null);

        mockMvc.perform(
                post("/api/saved-offers/" + offerId)
                        .header(
                                "Authorization",
                                auth(token)))
                .andExpect(status().isCreated());

        mockMvc.perform(
                post("/api/saved-offers/" + offerId)
                        .header(
                                "Authorization",
                                auth(token)))
                .andExpect(status().isConflict());
    }

    @Test
    void userShouldSeeOnlyOwnSavedOffers()
            throws Exception {

        String firstUserToken = registerAndLogin(
                "First User",
                uniqueEmail("first"));

        String secondUserToken = registerAndLogin(
                "Second User",
                uniqueEmail("second"));

        long offerId = createOffer(
                firstUserToken,
                "Private Saved Offer",
                new BigDecimal("15"),
                null);

        mockMvc.perform(
                post("/api/saved-offers/" + offerId)
                        .header(
                                "Authorization",
                                auth(firstUserToken)))
                .andExpect(status().isCreated());

        mockMvc.perform(
                get("/api/saved-offers")
                        .header(
                                "Authorization",
                                auth(secondUserToken)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data.length()")
                                .value(0));

        mockMvc.perform(
                get("/api/saved-offers/" + offerId)
                        .header(
                                "Authorization",
                                auth(secondUserToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void userShouldBeAbleToUnsaveOffer()
            throws Exception {

        String token = registerAndLogin(
                "Unsave User",
                uniqueEmail("unsave"));

        long offerId = createOffer(
                token,
                "Unsave Test Offer",
                new BigDecimal("10"),
                null);

        mockMvc.perform(
                post("/api/saved-offers/" + offerId)
                        .header(
                                "Authorization",
                                auth(token)))
                .andExpect(status().isCreated());

        mockMvc.perform(
                delete("/api/saved-offers/" + offerId)
                        .header(
                                "Authorization",
                                auth(token)))
                .andExpect(status().isOk());

        mockMvc.perform(
                get("/api/saved-offers/" + offerId)
                        .header(
                                "Authorization",
                                auth(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void savingNonExistingOfferShouldReturnNotFound()
            throws Exception {

        String token = registerAndLogin(
                "Missing Offer User",
                uniqueEmail("missing"));

        mockMvc.perform(
                post("/api/saved-offers/999999")
                        .header(
                                "Authorization",
                                auth(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void savedOffersShouldRequireAuthentication()
            throws Exception {

        mockMvc.perform(
                get("/api/saved-offers"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(
                post("/api/saved-offers/1"))
                .andExpect(status().isUnauthorized());
    }
}