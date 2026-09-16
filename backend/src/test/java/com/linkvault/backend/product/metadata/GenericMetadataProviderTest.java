package com.linkvault.backend.product.metadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class GenericMetadataProviderTest {

    @Mock
    private SafeUrlFetcher urlFetcher;

    private GenericMetadataProvider provider;

    private static final String PRODUCT_URL = "https://example.com/products/phone";

    @BeforeEach
    void setUp() {

        provider = new GenericMetadataProvider(
                urlFetcher,
                new ObjectMapper());
    }

    @Test
    void shouldSupportHttpAndHttpsUrls() {

        assertTrue(
                provider.supports(
                        "http://example.com/product"));

        assertTrue(
                provider.supports(
                        "https://example.com/product"));
    }

    @Test
    void shouldRejectUnsupportedUrls() {

        assertTrue(
                !provider.supports(
                        "ftp://example.com/product"));

        assertTrue(
                !provider.supports(
                        "example.com/product"));

        assertTrue(
                !provider.supports(null));

        assertTrue(
                !provider.supports("   "));
    }

    @Test
    void shouldExtractProductFromJsonLd() {

        String html = """
                <html>
                <head>
                    <title>Fallback Title</title>

                    <script type="application/ld+json">
                    {
                        "@context": "https://schema.org",
                        "@type": "Product",
                        "name": "Pixel Pro",
                        "description": "A premium smartphone",
                        "image": "https://example.com/pixel.jpg",
                        "category": "Smartphones",
                        "offers": {
                            "@type": "Offer",
                            "price": "79999",
                            "priceCurrency": "INR",
                            "seller": {
                                "@type": "Organization",
                                "name": "Example Store"
                            }
                        }
                    }
                    </script>
                </head>
                <body></body>
                </html>
                """;

        when(urlFetcher.fetch(PRODUCT_URL))
                .thenReturn(html);

        ProductMetadata metadata = provider.fetch(PRODUCT_URL);

        assertEquals(
                "Pixel Pro",
                metadata.name());

        assertEquals(
                "A premium smartphone",
                metadata.description());

        assertEquals(
                "https://example.com/pixel.jpg",
                metadata.imageUrl());

        assertEquals(
                "Smartphones",
                metadata.category());

        assertEquals(
                "Example Store",
                metadata.merchantName());

        assertEquals(
                new BigDecimal("79999"),
                metadata.price());

        assertEquals(
                "INR",
                metadata.currency());

        assertEquals(
                PRODUCT_URL,
                metadata.websiteUrl());
    }

    @Test
    void shouldExtractProductInsideGraph() {

        String html = """
                <html>
                <head>
                    <script type="application/ld+json">
                    {
                        "@context": "https://schema.org",
                        "@graph": [
                            {
                                "@type": "Organization",
                                "name": "Example"
                            },
                            {
                                "@type": "Product",
                                "name": "Graph Product",
                                "image": {
                                    "url": "https://example.com/graph.jpg"
                                },
                                "offers": {
                                    "price": "1499.50",
                                    "priceCurrency": "INR"
                                }
                            }
                        ]
                    }
                    </script>
                </head>
                </html>
                """;

        when(urlFetcher.fetch(PRODUCT_URL))
                .thenReturn(html);

        ProductMetadata metadata = provider.fetch(PRODUCT_URL);

        assertEquals(
                "Graph Product",
                metadata.name());

        assertEquals(
                "https://example.com/graph.jpg",
                metadata.imageUrl());

        assertEquals(
                new BigDecimal("1499.50"),
                metadata.price());

        assertEquals(
                "INR",
                metadata.currency());
    }

    @Test
    void shouldExtractProductFromJsonLdArray() {

        String html = """
                <html>
                <head>
                    <script type="application/ld+json">
                    [
                        {
                            "@type": "BreadcrumbList"
                        },
                        {
                            "@type": "Product",
                            "name": "Array Product",
                            "image": [
                                "https://example.com/one.jpg",
                                "https://example.com/two.jpg"
                            ]
                        }
                    ]
                    </script>
                </head>
                </html>
                """;

        when(urlFetcher.fetch(PRODUCT_URL))
                .thenReturn(html);

        ProductMetadata metadata = provider.fetch(PRODUCT_URL);

        assertEquals(
                "Array Product",
                metadata.name());

        assertEquals(
                "https://example.com/one.jpg",
                metadata.imageUrl());
    }

    @Test
    void shouldFallbackToOpenGraphAndStandardMetadata() {

        String html = """
                <html>
                <head>
                    <title>Product Page</title>

                    <meta property="og:title"
                          content="OG Product"/>

                    <meta property="og:description"
                          content="OG Description"/>

                    <meta property="og:image"
                          content="https://example.com/og.jpg"/>

                    <meta property="og:site_name"
                          content="Example Store"/>

                    <meta property="product:category"
                          content="Electronics"/>

                    <meta property="product:price:amount"
                          content="4999.99"/>

                    <meta property="product:price:currency"
                          content="INR"/>
                </head>
                </html>
                """;

        when(urlFetcher.fetch(PRODUCT_URL))
                .thenReturn(html);

        ProductMetadata metadata = provider.fetch(PRODUCT_URL);

        assertEquals(
                "OG Product",
                metadata.name());

        assertEquals(
                "OG Description",
                metadata.description());

        assertEquals(
                "https://example.com/og.jpg",
                metadata.imageUrl());

        assertEquals(
                "Example Store",
                metadata.merchantName());

        assertEquals(
                "Electronics",
                metadata.category());

        assertEquals(
                new BigDecimal("4999.99"),
                metadata.price());

        assertEquals(
                "INR",
                metadata.currency());
    }

    @Test
    void shouldUseFallbackMetadataPerField() {

        String html = """
                <html>
                <head>
                    <title>Fallback Product</title>

                    <script type="application/ld+json">
                    {
                        "@type": "Product",
                        "name": "JSON Product"
                    }
                    </script>

                    <meta property="og:image"
                          content="https://example.com/image.jpg"/>

                    <meta property="og:description"
                          content="Fallback Description"/>
                </head>
                </html>
                """;

        when(urlFetcher.fetch(PRODUCT_URL))
                .thenReturn(html);

        ProductMetadata metadata = provider.fetch(PRODUCT_URL);

        assertEquals(
                "JSON Product",
                metadata.name());

        assertEquals(
                "Fallback Description",
                metadata.description());

        assertEquals(
                "https://example.com/image.jpg",
                metadata.imageUrl());
    }

    @Test
    void shouldIgnoreMalformedJsonLdAndUseOtherMetadata() {

        String html = """
                <html>
                <head>

                    <script type="application/ld+json">
                    {
                        this-is-not-valid-json
                    </script>

                    <meta property="og:title"
                          content="Valid Product"/>

                </head>
                </html>
                """;

        when(urlFetcher.fetch(PRODUCT_URL))
                .thenReturn(html);

        ProductMetadata metadata = provider.fetch(PRODUCT_URL);

        assertEquals(
                "Valid Product",
                metadata.name());
    }

    @Test
    void shouldIgnoreMalformedPrice() {

        String html = """
                <html>
                <head>

                    <script type="application/ld+json">
                    {
                        "@type": "Product",
                        "name": "Test Product",
                        "offers": {
                            "price": "not-a-number",
                            "priceCurrency": "INR"
                        }
                    }
                    </script>

                </head>
                </html>
                """;

        when(urlFetcher.fetch(PRODUCT_URL))
                .thenReturn(html);

        ProductMetadata metadata = provider.fetch(PRODUCT_URL);

        assertEquals(
                "Test Product",
                metadata.name());

        assertNull(metadata.price());

        assertEquals(
                "INR",
                metadata.currency());
    }

    @Test
    void shouldRejectPageWithoutProductName() {

        String html = """
                <html>
                <head>
                    <script type="application/ld+json">
                    {
                        "@type": "Organization",
                        "name": "Example Store"
                    }
                    </script>
                </head>
                </html>
                """;

        when(urlFetcher.fetch(PRODUCT_URL))
                .thenReturn(html);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> provider.fetch(PRODUCT_URL));

        assertEquals(
                "Product information could not be identified",
                exception.getMessage());
    }

    @Test
    void shouldHandleMultipleJsonLdBlocks() {

        String html = """
                <html>
                <head>

                    <script type="application/ld+json">
                    {
                        "@type": "Organization",
                        "name": "Example Store"
                    }
                    </script>

                    <script type="application/ld+json">
                    {
                        "@type": "Product",
                        "name": "Second Block Product",
                        "image": "https://example.com/product.jpg"
                    }
                    </script>

                </head>
                </html>
                """;

        when(urlFetcher.fetch(PRODUCT_URL))
                .thenReturn(html);

        ProductMetadata metadata = provider.fetch(PRODUCT_URL);

        assertEquals(
                "Second Block Product",
                metadata.name());

        assertEquals(
                "https://example.com/product.jpg",
                metadata.imageUrl());
    }

    @Test
    void shouldExtractSellerFromOfferArray() {

        String html = """
                <html>
                <head>

                    <script type="application/ld+json">
                    {
                        "@type": "Product",
                        "name": "Multi Seller Product",
                        "offers": [
                            {
                                "@type": "Offer",
                                "price": "999",
                                "priceCurrency": "INR",
                                "seller": {
                                    "@type": "Organization",
                                    "name": "Seller One"
                                }
                            }
                        ]
                    }
                    </script>

                </head>
                </html>
                """;

        when(urlFetcher.fetch(PRODUCT_URL))
                .thenReturn(html);

        ProductMetadata metadata = provider.fetch(PRODUCT_URL);

        assertEquals(
                "Multi Seller Product",
                metadata.name());

        assertEquals(
                "Seller One",
                metadata.merchantName());

        assertEquals(
                new BigDecimal("999"),
                metadata.price());
    }
}