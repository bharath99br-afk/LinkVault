package com.linkvault.backend.merchant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MerchantRequest {

    @NotBlank(message = "Merchant name cannot be empty")
    @Size(min = 2, max = 100, message = "Merchant name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Website URL cannot be empty")
    private String websiteUrl;

    private Long globalMerchantId;

    public MerchantRequest() {
    }

    public MerchantRequest(String name, String websiteUrl, Long globalMerchantId) {
        this.name = name;
        this.websiteUrl = websiteUrl;
        this.globalMerchantId = globalMerchantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public Long getGlobalMerchantId() {
        return globalMerchantId;
    }

    public void setGlobalMerchantId(Long globalMerchantId) {
        this.globalMerchantId = globalMerchantId;
    }
}