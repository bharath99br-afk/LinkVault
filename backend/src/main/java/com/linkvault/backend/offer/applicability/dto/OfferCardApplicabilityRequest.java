package com.linkvault.backend.offer.applicability.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OfferCardApplicabilityRequest {

    @NotNull(message = "Bank ID cannot be null")
    private Long bankId;

    @NotBlank(message = "Card name cannot be empty")
    @Size(min = 2, max = 100, message = "Card name must be between 2 and 100 characters")
    private String cardName;

    public OfferCardApplicabilityRequest() {
    }

    public OfferCardApplicabilityRequest(
            Long bankId,
            String cardName) {

        this.bankId = bankId;
        this.cardName = cardName;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
}