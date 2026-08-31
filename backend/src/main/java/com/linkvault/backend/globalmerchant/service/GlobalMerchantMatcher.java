package com.linkvault.backend.globalmerchant.service;

import java.net.URI;
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

        Optional<GlobalMerchant> byName = repository.findByNameIgnoreCase(merchantName);

        if (byName.isPresent()) {
            return byName;
        }

        String normalizedWebsite = normalizeWebsite(websiteUrl);

        return repository.findAll()
                .stream()
                .filter(global -> normalizeWebsite(global.getWebsiteUrl())
                        .equalsIgnoreCase(normalizedWebsite))
                .findFirst();
    }

    private String normalizeWebsite(String url) {

        try {

            URI uri = URI.create(url);

            String host = uri.getHost();

            if (host == null) {
                return url.toLowerCase();
            }

            host = host.toLowerCase();

            if (host.startsWith("www.")) {
                host = host.substring(4);
            }

            return host;

        } catch (Exception exception) {

            return url.toLowerCase();
        }
    }
}