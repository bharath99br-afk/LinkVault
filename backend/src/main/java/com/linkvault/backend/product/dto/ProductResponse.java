package com.linkvault.backend.product.dto;

public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String category;
    private String websiteUrl;
    private Long merchantId;
    private String merchantName;
    private Long globalMerchantId;
    private String globalMerchantName;

    public ProductResponse() {
    }

    public ProductResponse(
            Long id,
            String name,
            String description,
            String imageUrl,
            String category,
            String websiteUrl,
            Long merchantId,
            String merchantName,
            Long globalMerchantId,
            String globalMerchantName) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.websiteUrl = websiteUrl;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.globalMerchantId = globalMerchantId;
        this.globalMerchantName = globalMerchantName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Long getGlobalMerchantId() {
        return globalMerchantId;
    }

    public void setGlobalMerchantId(Long globalMerchantId) {
        this.globalMerchantId = globalMerchantId;
    }

    public String getGlobalMerchantName() {
        return globalMerchantName;
    }

    public void setGlobalMerchantName(String globalMerchantName) {
        this.globalMerchantName = globalMerchantName;
    }
}