package com.linkvault.backend.offer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.linkvault.backend.offer.model.DiscountType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OfferRequest {

    @NotBlank(message = "Offer title cannot be empty")
    @Size(min = 2, max = 150, message = "Offer title must be between 2 and 150 characters")
    private String title;

    @Size(max = 500, message = "Offer description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Discount type cannot be null")
    private DiscountType discountType;

    @NotNull(message = "Discount value cannot be null")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.01", message = "Maximum discount must be greater than 0")
    private BigDecimal maxDiscount;

    @DecimalMin(value = "0.01", message = "Minimum transaction amount must be greater than 0")
    private BigDecimal minTransactionAmount;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;

    private Long globalMerchantId;

    public OfferRequest() {
    }

    public OfferRequest(
            String title,
            String description,
            DiscountType discountType,
            BigDecimal discountValue,
            BigDecimal maxDiscount,
            BigDecimal minTransactionAmount,
            LocalDate startDate,
            LocalDate endDate, Long globalMerchantId) {

        this.title = title;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.maxDiscount = maxDiscount;
        this.minTransactionAmount = minTransactionAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.globalMerchantId = globalMerchantId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(BigDecimal maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public BigDecimal getMinTransactionAmount() {
        return minTransactionAmount;
    }

    public void setMinTransactionAmount(BigDecimal minTransactionAmount) {
        this.minTransactionAmount = minTransactionAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getGlobalMerchantId() {
        return globalMerchantId;
    }

    public void setGlobalMerchantId(Long globalMerchantId) {
        this.globalMerchantId = globalMerchantId;
    }
}