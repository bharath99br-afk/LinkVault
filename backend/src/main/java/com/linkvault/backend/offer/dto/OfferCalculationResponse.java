package com.linkvault.backend.offer.dto;

import java.math.BigDecimal;

public class OfferCalculationResponse {

    private Long offerId;
    private Long cardId;

    private boolean eligible;
    private String reason;

    private BigDecimal transactionAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private BigDecimal savingsPercentage;

    public OfferCalculationResponse() {
    }

    public OfferCalculationResponse(
            Long offerId,
            Long cardId,
            boolean eligible,
            String reason,
            BigDecimal transactionAmount,
            BigDecimal discountAmount,
            BigDecimal finalAmount,
            BigDecimal savingsPercentage) {

        this.offerId = offerId;
        this.cardId = cardId;
        this.eligible = eligible;
        this.reason = reason;
        this.transactionAmount = transactionAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.savingsPercentage = savingsPercentage;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public BigDecimal getSavingsPercentage() {
        return savingsPercentage;
    }

    public void setSavingsPercentage(BigDecimal savingsPercentage) {
        this.savingsPercentage = savingsPercentage;
    }
}