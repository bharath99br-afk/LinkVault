package com.linkvault.backend.offer.applicability.dto;

import jakarta.validation.constraints.NotNull;

public class OfferCardApplicabilityRequest {

    @NotNull(message = "Card Product ID cannot be null")
    private Long cardProductId;

    public OfferCardApplicabilityRequest() {
    }

    public OfferCardApplicabilityRequest(Long cardProductId) {
        this.cardProductId = cardProductId;
    }

    public Long getCardProductId() {
        return cardProductId;
    }

    public void setCardProductId(Long cardProductId) {
        this.cardProductId = cardProductId;
    }
}