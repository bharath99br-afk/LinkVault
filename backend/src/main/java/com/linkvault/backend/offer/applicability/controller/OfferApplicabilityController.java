package com.linkvault.backend.offer.applicability.controller;

import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.offer.applicability.dto.OfferBankApplicabilityRequest;
import com.linkvault.backend.offer.applicability.dto.OfferCardApplicabilityRequest;
import com.linkvault.backend.offer.applicability.service.OfferApplicabilityService;
import com.linkvault.backend.util.ApiResponseUtil;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offers")
public class OfferApplicabilityController {

    private final OfferApplicabilityService service;

    public OfferApplicabilityController(
            OfferApplicabilityService service) {

        this.service = service;
    }

    @PostMapping("/{offerId}/banks")
    public ResponseEntity<ApiResponse<Object>> addBankApplicability(
            @PathVariable Long offerId,
            @Valid @RequestBody OfferBankApplicabilityRequest request) {

        service.addBankApplicability(offerId, request);

        return ApiResponseUtil.success(
                "Offer Bank Applicability Added Successfully",
                null);
    }

    @DeleteMapping("/{offerId}/banks/{bankId}")
    public ResponseEntity<ApiResponse<Object>> removeBankApplicability(
            @PathVariable Long offerId,
            @PathVariable Long bankId) {

        service.removeBankApplicability(
                offerId,
                bankId);

        return ApiResponseUtil.success(
                "Offer Bank Applicability Removed Successfully",
                null);
    }

    @PostMapping("/{offerId}/cards")
    public ResponseEntity<ApiResponse<Object>> addCardApplicability(
            @PathVariable Long offerId,
            @Valid @RequestBody OfferCardApplicabilityRequest request) {

        service.addCardApplicability(
                offerId,
                request);

        return ApiResponseUtil.success(
                "Offer Card Applicability Added Successfully",
                null);
    }

    @DeleteMapping("/{offerId}/cards")
    public ResponseEntity<ApiResponse<Object>> removeCardApplicability(
            @PathVariable Long offerId,
            @RequestParam Long bankId,
            @RequestParam String cardName) {

        service.removeCardApplicability(
                offerId,
                bankId,
                cardName);

        return ApiResponseUtil.success(
                "Offer Card Applicability Removed Successfully",
                null);
    }
}