package com.linkvault.backend.globalmerchant.service;

import java.net.URI;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.linkvault.backend.globalmerchant.model.GlobalMerchant;
import com.linkvault.backend.globalmerchant.repository.GlobalMerchantRepository;

@Service
public class GlobalMerchantMatcher {

    private final GlobalMerchantRepository repository;

    public GlobalMerchantMatcher(GlobalMerchantRepository repository) {
        this.repository = repository;
    }

    public Optional<GlobalMerchant> match(String merchantName, String websiteUrl) {

        if (merchantName != null && !merchantName.isBlank()) {
            Optional<GlobalMerchant> byName = repository.findByNameIgnoreCase(merchantName.trim());

            if (byName.isPresent()) {
                return byName;
            }
        }

        return matchByWebsiteUrl(websiteUrl);
    }

    public Optional<GlobalMerchant> matchByWebsiteUrl(String websiteUrl) {

        String normalizedWebsite = normalizeWebsite(websiteUrl);

        if (normalizedWebsite.isBlank()) {
            return Optional.empty();
        }

        return repository.findAll()
                .stream()
                .filter(global -> normalizeWebsite(global.getWebsiteUrl())
                        .equalsIgnoreCase(normalizedWebsite))
                .findFirst();
    }

    private String normalizeWebsite(String url) {

        if (url == null || url.isBlank()) {
            return "";
        }

        try {

            String candidate = url.trim();

            String lowerCandidate = candidate.toLowerCase(Locale.ROOT);

            if (!lowerCandidate.startsWith("http://") && !lowerCandidate.startsWith("https://")) {
                candidate = "https://" + candidate;
            }

            URI uri = URI.create(candidate);

            String host = uri.getHost();

            if (host == null) {
                return "";
            }

            host = host.toLowerCase(Locale.ROOT);

            if (host.startsWith("www.")) {
                host = host.substring(4);
            }

            return host;

        } catch (Exception exception) {

            return "";
        }
    }
}
