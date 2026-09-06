package com.linkvault.backend.deal.dto;

import java.math.BigDecimal;

public class DealOptionResponse {

    private Long offerId;
    private String offerTitle;

    private Long cardId;
    private String cardName;

    private Long bankId;
    private String bankName;

    private BigDecimal transactionAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private BigDecimal savingsPercentage;

    public DealOptionResponse() {
    }

    public DealOptionResponse(
            Long offerId,
            String offerTitle,
            Long cardId,
            String cardName,
            Long bankId,
            String bankName,
            BigDecimal transactionAmount,
            BigDecimal discountAmount,
            BigDecimal finalAmount,
            BigDecimal savingsPercentage) {

        this.offerId = offerId;
        this.offerTitle = offerTitle;
        this.cardId = cardId;
        this.cardName = cardName;
        this.bankId = bankId;
        this.bankName = bankName;
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

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
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