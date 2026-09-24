package com.linkvault.backend.savedoffer.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.savedoffer.dto.SavedOfferResponse;
import com.linkvault.backend.savedoffer.service.SavedOfferService;
import com.linkvault.backend.util.ApiResponseUtil;

@RestController
@RequestMapping("/api/saved-offers")
public class SavedOfferController {

    private final SavedOfferService savedOfferService;

    public SavedOfferController(
            SavedOfferService savedOfferService) {

        this.savedOfferService = savedOfferService;
    }

    @PostMapping("/{offerId}")
    public ResponseEntity<ApiResponse<SavedOfferResponse>> saveOffer(@PathVariable Long offerId) {

        SavedOfferResponse response = savedOfferService.saveOffer(offerId);

        return ApiResponseUtil.created(
                "Offer Saved Successfully",
                response);
    }

    @DeleteMapping("/{offerId}")
    public ResponseEntity<ApiResponse<Object>> unsaveOffer(@PathVariable Long offerId) {

        savedOfferService.unsaveOffer(offerId);

        return ApiResponseUtil.success(
                "Offer Removed From Saved Offers",
                null);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SavedOfferResponse>>> getSavedOffers() {

        List<SavedOfferResponse> response = savedOfferService.getSavedOffers();

        return ApiResponseUtil.success(
                "Saved Offers Found",
                response);
    }

    @GetMapping("/{offerId}")
    public ResponseEntity<ApiResponse<SavedOfferResponse>> getSavedOffer(@PathVariable Long offerId) {

        SavedOfferResponse response = savedOfferService.getSavedOffer(offerId);

        return ApiResponseUtil.success(
                "Saved Offer Found",
                response);
    }
}