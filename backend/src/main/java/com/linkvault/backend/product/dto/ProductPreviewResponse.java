package com.linkvault.backend.product.dto;

import com.linkvault.backend.product.metadata.ProductMetadata;

public record ProductPreviewResponse(
        String name,
        String description,
        String imageUrl,
        String category,
        String websiteUrl,
        String merchantName,
        java.math.BigDecimal price,
        String currency) {

    public static ProductPreviewResponse from(ProductMetadata metadata) {

        return new ProductPreviewResponse(
                metadata.name(),
                metadata.description(),
                metadata.imageUrl(),
                metadata.category(),
                metadata.websiteUrl(),
                metadata.merchantName(),
                metadata.price(),
                metadata.currency());
    }
}