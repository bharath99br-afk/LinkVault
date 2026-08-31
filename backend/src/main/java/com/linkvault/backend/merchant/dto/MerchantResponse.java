package com.linkvault.backend.merchant.dto;

public class MerchantResponse {

    private Long id;
    private String name;
    private String websiteUrl;

    private Long globalMerchantId;

    private String globalMerchantName;

    public MerchantResponse() {
    }

    public MerchantResponse(Long id, String name, String websiteUrl, Long globalMerchantId, String globalMerchantName) {
        this.id = id;
        this.name = name;
        this.websiteUrl = websiteUrl;
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

    public String getGlobalMerchantName() {
        return globalMerchantName;
    }

    public void setGlobalMerchantName(String globalMerchantName) {
        this.globalMerchantName = globalMerchantName;
    }
}