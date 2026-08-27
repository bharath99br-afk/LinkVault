package com.linkvault.backend.link.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LinkRequest {

    @NotBlank(message = "Title cannot be empty")
    @Size(min = 2, max = 100)
    private String title;

    @NotBlank(message = "URL cannot be empty")
    private String url;

    private Long merchantId;
    private Long productId;

    public LinkRequest() {
    }

    public LinkRequest(String title, String url) {
        this.title = title;
        this.url = url;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}