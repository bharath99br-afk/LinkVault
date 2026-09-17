package com.linkvault.backend.card.catalog.dto;

public record CardProductResponse(
        Long id,
        Long bankId,
        String bankName,
        String name,
        String cardType,
        boolean active) {
}