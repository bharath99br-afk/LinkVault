package com.linkvault.backend.product.metadata;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@Component
public class GenericMetadataProvider implements ProductDataProvider {

    private final SafeUrlFetcher urlFetcher;
    private final ObjectMapper objectMapper;

    public GenericMetadataProvider(
            SafeUrlFetcher urlFetcher,
            ObjectMapper objectMapper) {

        this.urlFetcher = urlFetcher;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String url) {

        if (url == null || url.isBlank()) {
            return false;
        }

        String normalizedUrl = url.trim().toLowerCase(Locale.ROOT);

        return normalizedUrl.startsWith("http://")
                || normalizedUrl.startsWith("https://");
    }

    @Override
    public ProductMetadata fetch(String url) {

        String html = urlFetcher.fetch(url);

        Document document = Jsoup.parse(html, url);

        List<JsonNode> productNodes = extractProductNodes(document);

        String name = firstNonBlank(
                extractJsonLdName(productNodes),
                extractMeta(document, "og:title"),
                extractMeta(document, "twitter:title"),
                document.title());

        String description = firstNonBlank(
                extractJsonLdDescription(productNodes),
                extractMeta(document, "og:description"),
                extractMetaByName(document, "description"));

        String imageUrl = firstNonBlank(
                extractJsonLdImage(productNodes),
                extractMeta(document, "og:image"),
                extractMeta(document, "og:image:url"),
                extractMeta(document, "twitter:image"));

        String category = firstNonBlank(
                extractJsonLdCategory(productNodes),
                extractMeta(document, "product:category"));

        String merchantName = firstNonBlank(
                extractJsonLdMerchant(productNodes),
                extractMeta(document, "og:site_name"));

        BigDecimal price = firstNonNull(
                extractJsonLdPrice(productNodes),
                extractMetaPrice(document));

        String currency = firstNonBlank(
                extractJsonLdCurrency(productNodes),
                extractMeta(document, "product:price:currency"));

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Product information could not be identified");
        }

        return new ProductMetadata(
                name.trim(),
                normalize(description),
                normalize(imageUrl),
                normalize(category),
                url,
                normalize(merchantName),
                price,
                normalize(currency));
    }

    /**
     * Finds every JSON-LD Product node.
     *
     * Supports:
     * - direct Product objects
     * - arrays
     * - @graph structures
     * - multiple JSON-LD script blocks
     */
    private List<JsonNode> extractProductNodes(Document document) {

        List<JsonNode> products = new ArrayList<>();

        for (Element element : document.select("script[type=application/ld+json]")) {

            String data = element.data();

            if (data == null || data.isBlank()) {
                continue;
            }

            try {
                JsonNode root = objectMapper.readTree(data);

                collectProductNodes(root, products);

            } catch (RuntimeException exception) {
                /*
                 * One malformed JSON-LD block should not prevent
                 * other metadata sources from being processed.
                 */
            }
        }

        return products;
    }

    private void collectProductNodes(
            JsonNode node,
            List<JsonNode> products) {

        if (node == null || node.isNull()) {
            return;
        }

        if (node.isArray()) {

            for (JsonNode child : node) {
                collectProductNodes(child, products);
            }

            return;
        }

        if (!node.isObject()) {
            return;
        }

        if (isProductNode(node)) {
            products.add(node);
        }

        JsonNode graph = node.get("@graph");

        if (graph != null) {
            collectProductNodes(graph, products);
        }
    }

    private boolean isProductNode(JsonNode node) {

        JsonNode type = node.get("@type");

        if (type == null) {
            return false;
        }

        if (type.isTextual()) {
            return "Product".equalsIgnoreCase(type.asText());
        }

        if (type.isArray()) {

            for (JsonNode typeNode : type) {

                if (typeNode.isTextual()
                        && "Product".equalsIgnoreCase(typeNode.asText())) {

                    return true;
                }
            }
        }

        return false;
    }

    private String extractJsonLdName(
            List<JsonNode> products) {

        for (JsonNode product : products) {

            String value = textValue(product.get("name"));

            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return null;
    }

    private String extractJsonLdDescription(
            List<JsonNode> products) {

        for (JsonNode product : products) {

            String value = textValue(product.get("description"));

            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return null;
    }

    private String extractJsonLdImage(
            List<JsonNode> products) {

        for (JsonNode product : products) {

            JsonNode image = product.get("image");

            String imageUrl = extractImageValue(image);

            if (imageUrl != null && !imageUrl.isBlank()) {
                return imageUrl.trim();
            }
        }

        return null;
    }

    private String extractImageValue(JsonNode image) {

        if (image == null || image.isNull()) {
            return null;
        }

        if (image.isTextual()) {
            return image.asText();
        }

        if (image.isArray()) {

            for (JsonNode imageNode : image) {

                String value = extractImageValue(imageNode);

                if (value != null && !value.isBlank()) {
                    return value;
                }
            }

            return null;
        }

        if (image.isObject()) {

            String value = firstNonBlank(
                    textValue(image.get("url")),
                    textValue(image.get("contentUrl")));

            return value;
        }

        return null;
    }

    private String extractJsonLdCategory(
            List<JsonNode> products) {

        for (JsonNode product : products) {

            String value = textValue(product.get("category"));

            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return null;
    }

    private String extractJsonLdMerchant(
            List<JsonNode> products) {

        for (JsonNode product : products) {

            JsonNode offers = product.get("offers");

            String sellerName = extractSellerName(offers);

            if (sellerName != null && !sellerName.isBlank()) {
                return sellerName.trim();
            }
        }

        return null;
    }

    private String extractSellerName(JsonNode offers) {

        if (offers == null || offers.isNull()) {
            return null;
        }

        if (offers.isArray()) {

            for (JsonNode offer : offers) {

                String value = extractSellerName(offer);

                if (value != null && !value.isBlank()) {
                    return value;
                }
            }

            return null;
        }

        if (!offers.isObject()) {
            return null;
        }

        JsonNode seller = offers.get("seller");

        if (seller == null || seller.isNull()) {
            return null;
        }

        if (seller.isTextual()) {
            return seller.asText();
        }

        if (seller.isObject()) {
            return textValue(seller.get("name"));
        }

        return null;
    }

    private BigDecimal extractJsonLdPrice(
            List<JsonNode> products) {

        for (JsonNode product : products) {

            BigDecimal price = extractPriceFromOffers(
                    product.get("offers"));

            if (price != null) {
                return price;
            }
        }

        return null;
    }

    private BigDecimal extractPriceFromOffers(
            JsonNode offers) {

        if (offers == null || offers.isNull()) {
            return null;
        }

        if (offers.isArray()) {

            for (JsonNode offer : offers) {

                BigDecimal price = extractPriceFromOffers(offer);

                if (price != null) {
                    return price;
                }
            }

            return null;
        }

        if (!offers.isObject()) {
            return null;
        }

        BigDecimal price = parseDecimal(
                textValue(offers.get("price")));

        if (price != null) {
            return price;
        }

        return parseDecimal(
                textValue(offers.get("lowPrice")));
    }

    private String extractJsonLdCurrency(
            List<JsonNode> products) {

        for (JsonNode product : products) {

            String currency = extractCurrencyFromOffers(
                    product.get("offers"));

            if (currency != null && !currency.isBlank()) {
                return currency.trim();
            }
        }

        return null;
    }

    private String extractCurrencyFromOffers(
            JsonNode offers) {

        if (offers == null || offers.isNull()) {
            return null;
        }

        if (offers.isArray()) {

            for (JsonNode offer : offers) {

                String currency = extractCurrencyFromOffers(offer);

                if (currency != null && !currency.isBlank()) {
                    return currency;
                }
            }

            return null;
        }

        if (!offers.isObject()) {
            return null;
        }

        return textValue(
                offers.get("priceCurrency"));
    }

    private String extractMeta(
            Document document,
            String property) {

        String value = document.select(
                "meta[property=" + property + "]")
                .attr("content");

        return value.isBlank()
                ? null
                : value.trim();
    }

    private String extractMetaByName(
            Document document,
            String name) {

        String value = document.select(
                "meta[name=" + name + "]")
                .attr("content");

        return value.isBlank()
                ? null
                : value.trim();
    }

    private BigDecimal extractMetaPrice(
            Document document) {

        String price = extractMeta(
                document,
                "product:price:amount");

        return parseDecimal(price);
    }

    private String textValue(JsonNode node) {

        if (node == null
                || node.isNull()
                || !node.isValueNode()) {

            return null;
        }

        String value = node.asText();

        return value == null || value.isBlank()
                ? null
                : value.trim();
    }

    private BigDecimal parseDecimal(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String firstNonBlank(String... values) {

        for (String value : values) {

            if (value != null && !value.isBlank()) {
                return value;
            }
        }

        return null;
    }

    private <T> T firstNonNull(T... values) {

        for (T value : values) {

            if (value != null) {
                return value;
            }
        }

        return null;
    }
}