package com.linkvault.backend.link.dto;

public class LinkResponse {

    private Long id;
    private String title;
    private String url;
    private Long merchantId;
    private String merchantName;

    public LinkResponse() {
    }

    public LinkResponse(Long id, String title, String url, Long merchantId, String merchantName) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
}