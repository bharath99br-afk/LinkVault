package com.linkvault.backend.offer.applicability.dto;

public record OfferCardApplicabilityResponse(
        Long id,
        Long offerId,
        Long bankId,
        String bankName,
        Long cardProductId,
        String cardProductName) {
}