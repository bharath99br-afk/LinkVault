package com.linkvault.backend.offer.applicability.dto;

public record OfferBankApplicabilityResponse(
        Long id,
        Long offerId,
        Long bankId,
        String bankName) {
}