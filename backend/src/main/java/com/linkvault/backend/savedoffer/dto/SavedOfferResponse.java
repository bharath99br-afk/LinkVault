package com.linkvault.backend.savedoffer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.linkvault.backend.offer.model.DiscountType;

public class SavedOfferResponse {

    private Long id;
    private Long offerId;
    private String title;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscount;
    private BigDecimal minTransactionAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long globalMerchantId;
    private String globalMerchantName;

    public SavedOfferResponse() {
    }

    public SavedOfferResponse(
            Long id,
            Long offerId,
            String title,
            String description,
            DiscountType discountType,
            BigDecimal discountValue,
            BigDecimal maxDiscount,
            BigDecimal minTransactionAmount,
            LocalDate startDate,
            LocalDate endDate,
            Long globalMerchantId,
            String globalMerchantName) {

        this.id = id;
        this.offerId = offerId;
        this.title = title;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.maxDiscount = maxDiscount;
        this.minTransactionAmount = minTransactionAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.globalMerchantId = globalMerchantId;
        this.globalMerchantName = globalMerchantName;
    }

    public Long getId() {
        return id;
    }

    public Long getOfferId() {
        return offerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public BigDecimal getMaxDiscount() {
        return maxDiscount;
    }

    public BigDecimal getMinTransactionAmount() {
        return minTransactionAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Long getGlobalMerchantId() {
        return globalMerchantId;
    }

    public String getGlobalMerchantName() {
        return globalMerchantName;
    }
}