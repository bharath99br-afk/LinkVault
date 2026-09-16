package com.linkvault.backend.product;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.linkvault.backend.IntegrationTestBase;
import com.linkvault.backend.product.metadata.SafeUrlFetcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

class ProductPreviewIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SafeUrlFetcher safeUrlFetcher;

    private static final String PRODUCT_URL = "https://example.com/product/iphone-17";

    private static final String PRODUCT_HTML = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>iPhone 17</title>

                <meta property="og:title"
                      content="iPhone 17">

                <meta property="og:description"
                      content="Latest iPhone with advanced features">

                <meta property="og:image"
                      content="https://example.com/images/iphone-17.jpg">

                <meta property="og:site_name"
                      content="Example Store">

                <meta property="product:category"
                      content="Smartphones">

                <meta property="product:price:amount"
                      content="79999">

                <meta property="product:price:currency"
                      content="INR">

                <script type="application/ld+json">
                {
                    "@context": "https://schema.org",
                    "@type": "Product",
                    "name": "iPhone 17",
                    "description": "Latest iPhone with advanced features"
                }
                </script>
            </head>
            <body>
                <h1>iPhone 17</h1>
            </body>
            </html>
            """;

    @BeforeEach
    void setUp() {
        when(safeUrlFetcher.fetch(PRODUCT_URL))
                .thenReturn(PRODUCT_HTML);
    }

    @Test
    void validProductUrlShouldReturnPreview() throws Exception {

        String email = uniqueEmail("preview");

        String token = registerAndLogin(
                "Preview User",
                email);

        String requestBody = """
                {
                    "url": "%s"
                }
                """.formatted(PRODUCT_URL);

        mockMvc.perform(
                post("/api/products/preview")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("iPhone 17"))
                .andExpect(jsonPath("$.data.description")
                        .value("Latest iPhone with advanced features"))
                .andExpect(jsonPath("$.data.imageUrl")
                        .value("https://example.com/images/iphone-17.jpg"))
                .andExpect(jsonPath("$.data.category")
                        .value("Smartphones"))
                .andExpect(jsonPath("$.data.websiteUrl")
                        .value(PRODUCT_URL))
                .andExpect(jsonPath("$.data.merchantName")
                        .value("Example Store"))
                .andExpect(jsonPath("$.data.price").value(79999))
                .andExpect(jsonPath("$.data.currency").value("INR"));
    }

    @Test
    void previewWithoutJwtShouldReturnUnauthorized() throws Exception {

        String requestBody = """
                {
                    "url": "%s"
                }
                """.formatted(PRODUCT_URL);

        mockMvc.perform(
                post("/api/products/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void missingUrlShouldReturnBadRequest() throws Exception {

        String email = uniqueEmail("missing-url");

        String token = registerAndLogin(
                "Missing URL User",
                email);

        mockMvc.perform(
                post("/api/products/preview")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void blankUrlShouldReturnBadRequest() throws Exception {

        String email = uniqueEmail("blank-url");

        String token = registerAndLogin(
                "Blank URL User",
                email);

        mockMvc.perform(
                post("/api/products/preview")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "url": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void malformedJsonShouldReturnBadRequest() throws Exception {

        String email = uniqueEmail("malformed-json");

        String token = registerAndLogin(
                "Malformed JSON User",
                email);

        mockMvc.perform(
                post("/api/products/preview")
                        .header("Authorization", auth(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"url":
                                """))
                .andExpect(status().isBadRequest());
    }
}