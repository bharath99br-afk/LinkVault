package com.linkvault.backend.offer.applicability.dto;

import jakarta.validation.constraints.NotNull;

public class OfferBankApplicabilityRequest {

    @NotNull(message = "Bank ID cannot be null")
    private Long bankId;

    public OfferBankApplicabilityRequest() {
    }

    public OfferBankApplicabilityRequest(Long bankId) {
        this.bankId = bankId;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }
}