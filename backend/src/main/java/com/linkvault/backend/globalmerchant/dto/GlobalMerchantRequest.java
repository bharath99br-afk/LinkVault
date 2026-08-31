package com.linkvault.backend.globalmerchant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GlobalMerchantRequest {

    @NotBlank(message = "Global merchant name cannot be empty")
    @Size(min = 2, max = 100, message = "Global merchant name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Website URL cannot be empty")
    private String websiteUrl;

    public GlobalMerchantRequest() {
    }

    public GlobalMerchantRequest(String name, String websiteUrl) {
        this.name = name;
        this.websiteUrl = websiteUrl;
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
}