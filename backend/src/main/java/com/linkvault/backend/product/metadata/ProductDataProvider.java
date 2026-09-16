package com.linkvault.backend.product.metadata;

public interface ProductDataProvider {

    ProductMetadata fetch(String url);

    boolean supports(String url);
}