package com.linkvault.backend.product.metadata;

public record ProductMetadata(
        String name,
        String description,
        String imageUrl,
        String category,
        String websiteUrl,
        String merchantName,
        java.math.BigDecimal price,
        String currency) {
}