package com.linkvault.backend.offer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.offer.dto.OfferCalculationRequest;
import com.linkvault.backend.offer.dto.OfferCalculationResponse;
import com.linkvault.backend.offer.service.OfferCalculationService;
import com.linkvault.backend.util.ApiResponseUtil;

@RestController
@RequestMapping("/api/offers")
public class OfferCalculationController {

    private final OfferCalculationService offerCalculationService;

    public OfferCalculationController(
            OfferCalculationService offerCalculationService) {

        this.offerCalculationService = offerCalculationService;
    }

    @PostMapping("/{offerId}/calculate")
    public ResponseEntity<ApiResponse<OfferCalculationResponse>> calculateDeal(
            @PathVariable Long offerId,
            @Valid @RequestBody OfferCalculationRequest request) {

        OfferCalculationResponse response = offerCalculationService.calculate(
                offerId,
                request);

        String message = response.isEligible()
                ? "Deal Calculated Successfully"
                : "Offer Is Not Eligible";

        return ApiResponseUtil.success(
                message,
                response);
    }
}