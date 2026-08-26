package com.linkvault.backend.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductRequest {

    @NotBlank(message = "Product name cannot be empty")
    @Size(min = 2, max = 150, message = "Product name must be between 2 and 150 characters")
    private String name;

    @Size(max = 500, message = "Product description cannot exceed 500 characters")
    private String description;

    private String imageUrl;

    @Size(max = 100, message = "Product category cannot exceed 100 characters")
    private String category;

    @NotBlank(message = "Website URL cannot be empty")
    private String websiteUrl;

    private Long merchantId;

    public ProductRequest() {
    }

    public ProductRequest(
            String name,
            String description,
            String imageUrl,
            String category,
            String websiteUrl,
            Long merchantId) {

        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.websiteUrl = websiteUrl;
        this.merchantId = merchantId;
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
}