package com.linkvault.backend.auth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * A small per-process guard for repeated login requests from one remote address.
 * Hosted multi-instance deployments should enforce the same policy at a shared
 * gateway or with a shared rate-limit store.
 */
@Service
public class LoginRateLimiter {

    private final int maxAttempts;
    private final Duration window;
    private final int maxTrackedClients;
    private final Map<String, RequestWindow> requestWindows = new HashMap<>();

    public LoginRateLimiter(
            @Value("${app.auth.login-rate-limit.max-attempts:10}") int maxAttempts,
            @Value("${app.auth.login-rate-limit.window-ms:900000}") long windowMs,
            @Value("${app.auth.login-rate-limit.max-tracked-clients:10000}") int maxTrackedClients) {

        if (maxAttempts < 1 || windowMs < 1 || maxTrackedClients < 1) {
            throw new IllegalArgumentException("Login rate-limit settings must be positive");
        }

        this.maxAttempts = maxAttempts;
        this.window = Duration.ofMillis(windowMs);
        this.maxTrackedClients = maxTrackedClients;
    }

    /**
     * Records one attempt and returns the remaining wait when the address has
     * reached its limit. Login traffic is low volume, so synchronized updates
     * keep each fixed-window count atomic without another dependency.
     */
    public synchronized Optional<Duration> tryAcquire(String remoteAddress) {
        Instant now = Instant.now();
        String clientKey = normalizeRemoteAddress(remoteAddress);

        RequestWindow requestWindow = requestWindows.get(clientKey);
        if (requestWindow != null && isExpired(requestWindow, now)) {
            requestWindows.remove(clientKey);
            requestWindow = null;
        }

        if (requestWindow == null) {
            if (requestWindows.size() >= maxTrackedClients) {
                removeExpiredWindows(now);
            }

            if (requestWindows.size() >= maxTrackedClients) {
                return Optional.of(timeUntilNextWindow(now));
            }

            requestWindow = new RequestWindow(now);
            requestWindows.put(clientKey, requestWindow);
        }

        if (requestWindow.attempts >= maxAttempts) {
            return Optional.of(Duration.between(now, requestWindow.startedAt.plus(window)));
        }

        requestWindow.attempts++;
        return Optional.empty();
    }

    private String normalizeRemoteAddress(String remoteAddress) {
        if (remoteAddress == null || remoteAddress.isBlank()) {
            return "<unknown>";
        }
        return remoteAddress.trim();
    }

    private boolean isExpired(RequestWindow requestWindow, Instant now) {
        return !now.isBefore(requestWindow.startedAt.plus(window));
    }

    private void removeExpiredWindows(Instant now) {
        Iterator<Map.Entry<String, RequestWindow>> iterator = requestWindows.entrySet().iterator();
        while (iterator.hasNext()) {
            if (isExpired(iterator.next().getValue(), now)) {
                iterator.remove();
            }
        }
    }

    private Duration timeUntilNextWindow(Instant now) {
        Instant earliestExpiry = requestWindows.values().stream()
                .map(requestWindow -> requestWindow.startedAt.plus(window))
                .min(Instant::compareTo)
                .orElse(now.plus(window));
        return Duration.between(now, earliestExpiry);
    }

    private static final class RequestWindow {
        private final Instant startedAt;
        private int attempts;

        private RequestWindow(Instant startedAt) {
            this.startedAt = startedAt;
        }
    }
}
