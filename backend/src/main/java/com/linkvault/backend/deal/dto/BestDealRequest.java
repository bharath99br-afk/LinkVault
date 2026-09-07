package com.linkvault.backend.deal.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class BestDealRequest {

    private Long linkId;

    @NotNull(message = "Transaction amount cannot be null")
    @DecimalMin(value = "0.01", message = "Transaction amount must be greater than 0")
    private BigDecimal transactionAmount;

    public BestDealRequest() {
    }

    public BestDealRequest(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BestDealRequest(Long linkId, BigDecimal transactionAmount) {
        this.linkId = linkId;
        this.transactionAmount = transactionAmount;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}