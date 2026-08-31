package com.linkvault.backend.globalmerchant.dto;

public class GlobalMerchantResponse {

    private Long id;
    private String name;
    private String websiteUrl;

    public GlobalMerchantResponse() {
    }

    public GlobalMerchantResponse(Long id, String name, String websiteUrl) {
        this.id = id;
        this.name = name;
        this.websiteUrl = websiteUrl;
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

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
}