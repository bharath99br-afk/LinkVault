package com.linkvault.backend.product.metadata;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Component
public class SafeHttpUrlFetcher implements SafeUrlFetcher {

    private static final int CONNECT_TIMEOUT_MILLIS = 5_000;
    private static final int READ_TIMEOUT_MILLIS = 10_000;

    private static final int MAX_RESPONSE_SIZE = 2 * 1024 * 1024;

    private static final int BUFFER_SIZE = 8 * 1024;

    @Override
    public String fetch(String url) {

        URI uri = validateUrl(url);

        validateHost(uri.getHost());

        HttpURLConnection connection = null;

        try {
            URL targetUrl = uri.toURL();

            connection = (HttpURLConnection) targetUrl.openConnection();

            connection.setRequestMethod("GET");

            connection.setConnectTimeout(
                    CONNECT_TIMEOUT_MILLIS);

            connection.setReadTimeout(
                    READ_TIMEOUT_MILLIS);

            /*
             * Never follow redirects automatically.
             *
             * A redirect could send the backend from a public
             * URL to an internal/private address.
             */
            connection.setInstanceFollowRedirects(false);

            connection.setRequestProperty(
                    "User-Agent",
                    "LinkVault/1.0 ProductMetadataFetcher");

            connection.setRequestProperty(
                    "Accept",
                    "text/html,application/xhtml+xml");

            connection.connect();

            int statusCode = connection.getResponseCode();

            if (statusCode >= 300
                    && statusCode < 400) {

                throw new IllegalArgumentException(
                        "Redirects are not supported for product preview");
            }

            if (statusCode < 200
                    || statusCode >= 300) {

                throw new IllegalArgumentException(
                        "Product page could not be retrieved");
            }

            String contentType = connection.getContentType();

            if (contentType == null
                    || !contentType
                            .toLowerCase(Locale.ROOT)
                            .contains("text/html")) {

                throw new IllegalArgumentException(
                        "URL does not point to an HTML page");
            }

            long contentLength = connection.getContentLengthLong();

            if (contentLength > MAX_RESPONSE_SIZE) {

                throw new IllegalArgumentException(
                        "Product page is too large to process");
            }

            try (InputStream inputStream = connection.getInputStream()) {

                byte[] body = readLimitedResponse(inputStream);

                return new String(
                        body,
                        StandardCharsets.UTF_8);
            }

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Could not retrieve product page",
                    exception);

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private byte[] readLimitedResponse(
            InputStream inputStream)
            throws IOException {

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] buffer = new byte[BUFFER_SIZE];

        int totalBytes = 0;

        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {

            totalBytes += bytesRead;

            if (totalBytes > MAX_RESPONSE_SIZE) {

                throw new IllegalArgumentException(
                        "Product page is too large to process");
            }

            output.write(
                    buffer,
                    0,
                    bytesRead);
        }

        return output.toByteArray();
    }

    private URI validateUrl(String url) {

        if (url == null || url.isBlank()) {

            throw new IllegalArgumentException(
                    "Product URL is required");
        }

        final URI uri;

        try {

            uri = URI.create(url.trim());

        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "Invalid product URL");
        }

        String scheme = uri.getScheme();

        if (!"http".equalsIgnoreCase(scheme)
                && !"https".equalsIgnoreCase(scheme)) {

            throw new IllegalArgumentException(
                    "Only HTTP and HTTPS URLs are supported");
        }

        if (uri.getUserInfo() != null) {

            throw new IllegalArgumentException(
                    "Product URL must not contain user information");
        }

        if (uri.getHost() == null
                || uri.getHost().isBlank()) {

            throw new IllegalArgumentException(
                    "Product URL must contain a valid host");
        }

        return uri;
    }

    private void validateHost(String host) {

        String normalizedHost = host.toLowerCase(Locale.ROOT);

        if ("localhost".equals(normalizedHost)
                || normalizedHost.endsWith(".localhost")
                || normalizedHost.endsWith(".local")) {

            throw new IllegalArgumentException(
                    "Local hosts are not allowed");
        }

        final InetAddress[] addresses;

        try {

            addresses = InetAddress.getAllByName(host);

        } catch (UnknownHostException exception) {

            throw new IllegalArgumentException(
                    "Product host could not be resolved");
        }

        if (addresses.length == 0) {

            throw new IllegalArgumentException(
                    "Product host could not be resolved");
        }

        for (InetAddress address : addresses) {

            if (isDisallowedAddress(address)) {

                throw new IllegalArgumentException(
                        "Private or local hosts are not allowed");
            }
        }
    }

    private boolean isDisallowedAddress(
            InetAddress address) {

        return address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress()
                || isCarrierGradeNat(address)
                || isBenchmarkNetwork(address);
    }

    private boolean isCarrierGradeNat(
            InetAddress address) {

        byte[] bytes = address.getAddress();

        if (bytes.length != 4) {
            return false;
        }

        int first = Byte.toUnsignedInt(bytes[0]);

        int second = Byte.toUnsignedInt(bytes[1]);

        return first == 100
                && second >= 64
                && second <= 127;
    }

    private boolean isBenchmarkNetwork(
            InetAddress address) {

        byte[] bytes = address.getAddress();

        if (bytes.length != 4) {
            return false;
        }

        int first = Byte.toUnsignedInt(bytes[0]);

        int second = Byte.toUnsignedInt(bytes[1]);

        return first == 198
                && second >= 18
                && second <= 19;
    }
}