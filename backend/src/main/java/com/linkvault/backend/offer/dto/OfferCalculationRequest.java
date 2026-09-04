package com.linkvault.backend.offer.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class OfferCalculationRequest {

    @NotNull(message = "Card ID cannot be null")
    private Long cardId;

    @NotNull(message = "Transaction amount cannot be null")
    @DecimalMin(value = "0.01", message = "Transaction amount must be greater than 0")
    private BigDecimal transactionAmount;

    public OfferCalculationRequest() {
    }

    public OfferCalculationRequest(
            Long cardId,
            BigDecimal transactionAmount) {

        this.cardId = cardId;
        this.transactionAmount = transactionAmount;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}