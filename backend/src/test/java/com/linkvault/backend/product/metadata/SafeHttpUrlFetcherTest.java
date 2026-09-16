package com.linkvault.backend.product.metadata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SafeHttpUrlFetcherTest {

    private final SafeHttpUrlFetcher fetcher = new SafeHttpUrlFetcher();

    @Test
    void shouldRejectNullUrl() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fetcher.fetch(null));

        assertTrue(
                exception.getMessage()
                        .contains("Product URL is required"));
    }

    @Test
    void shouldRejectBlankUrl() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fetcher.fetch("   "));

        assertTrue(
                exception.getMessage()
                        .contains("Product URL is required"));
    }

    @Test
    void shouldRejectInvalidUrl() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fetcher.fetch("not-a-url"));

        assertTrue(
                exception.getMessage()
                        .contains("Only HTTP and HTTPS URLs are supported"));
    }

    @Test
    void shouldRejectFileScheme() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fetcher.fetch("file:///etc/passwd"));

        assertTrue(
                exception.getMessage()
                        .contains("Only HTTP and HTTPS URLs are supported"));
    }

    @Test
    void shouldRejectLocalhost() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fetcher.fetch("http://localhost:8080"));

        assertTrue(
                exception.getMessage()
                        .contains("Local hosts are not allowed"));
    }

    @Test
    void shouldRejectLocalhostSubdomain() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fetcher.fetch("http://anything.localhost"));

        assertTrue(
                exception.getMessage()
                        .contains("Local hosts are not allowed"));
    }

    @Test
    void shouldRejectLocalDomain() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fetcher.fetch("http://internal.local"));

        assertTrue(
                exception.getMessage()
                        .contains("Local hosts are not allowed"));
    }

    @Test
    void shouldRejectLoopbackIp() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fetcher.fetch("http://127.0.0.1"));

        assertTrue(
                exception.getMessage()
                        .contains("Private or local hosts are not allowed"));
    }

    @Test
    void shouldRejectPrivateIp() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fetcher.fetch("http://192.168.1.1"));

        assertTrue(
                exception.getMessage()
                        .contains("Private or local hosts are not allowed"));
    }

    @Test
    void shouldRejectAnotherPrivateIpRange() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fetcher.fetch("http://10.0.0.1"));

        assertTrue(
                exception.getMessage()
                        .contains("Private or local hosts are not allowed"));
    }

    @Test
    void shouldRejectLinkLocalIp() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fetcher.fetch("http://169.254.1.1"));

        assertTrue(
                exception.getMessage()
                        .contains("Private or local hosts are not allowed"));
    }

    @Test
    void shouldRejectUserInfoInUrl() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fetcher.fetch(
                        "http://user:password@example.com"));

        assertTrue(
                exception.getMessage()
                        .contains("user information"));
    }
}