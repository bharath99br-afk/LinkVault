package com.linkvault.backend.product.service;

import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.product.dto.ProductPreviewResponse;
import com.linkvault.backend.product.metadata.ProductDataProvider;
import com.linkvault.backend.product.metadata.ProductMetadata;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductPreviewService {

    private final List<ProductDataProvider> providers;

    public ProductPreviewService(
            List<ProductDataProvider> providers) {

        this.providers = providers;
    }

    public ProductPreviewResponse preview(String url) {

        ProductDataProvider provider = providers.stream()
                .filter(candidate -> candidate.supports(url))
                .findFirst()
                .orElseThrow(() -> new LinkNotFoundException(
                        "No product data provider supports this URL"));

        ProductMetadata metadata = provider.fetch(url);

        if (metadata == null) {
            throw new LinkNotFoundException(
                    "Product information could not be found");
        }

        return ProductPreviewResponse.from(metadata);
    }
}