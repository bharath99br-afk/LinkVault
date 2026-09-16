package com.linkvault.backend.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductPreviewRequest(

        @NotBlank(message = "Product URL is required") @Size(max = 2000, message = "Product URL must not exceed 2000 characters") String url) {
}