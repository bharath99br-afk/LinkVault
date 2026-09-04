package com.linkvault.backend.offer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.util.ApiResponseUtil;
import com.linkvault.backend.offer.dto.OfferEligibilityRequest;
import com.linkvault.backend.offer.dto.OfferEligibilityResponse;
import com.linkvault.backend.offer.service.OfferEligibilityService;

@RestController
@RequestMapping("/api/offers")
public class OfferEligibilityController {

    private final OfferEligibilityService offerEligibilityService;

    public OfferEligibilityController(
            OfferEligibilityService offerEligibilityService) {

        this.offerEligibilityService = offerEligibilityService;
    }

    @PostMapping("/{offerId}/eligibility")
    public ResponseEntity<ApiResponse<OfferEligibilityResponse>> checkEligibility(
            @PathVariable Long offerId,
            @Valid @RequestBody OfferEligibilityRequest request) {

        OfferEligibilityResponse response = offerEligibilityService.checkEligibility(
                offerId,
                request);

        String message = response.isEligible()
                ? "Offer Is Eligible"
                : "Offer Is Not Eligible";

        return ApiResponseUtil.success(
                message,
                response);
    }
}