package com.linkvault.backend.offer.service;

import org.springframework.stereotype.Service;

import com.linkvault.backend.offer.dto.OfferIngestionRequest;
import com.linkvault.backend.offer.dto.OfferRequest;
import com.linkvault.backend.offer.dto.OfferResponse;

@Service
public class OfferIngestionService {

    private final OfferService offerService;

    public OfferIngestionService(OfferService offerService) {
        this.offerService = offerService;
    }

    public OfferResponse ingestOffer(OfferIngestionRequest request) {

        String title = request.getTitle() != null
                ? request.getTitle().trim()
                : null;

        String description = request.getDescription() != null
                ? request.getDescription().trim()
                : null;

        String sourceUrl = request.getSourceUrl() != null
                ? request.getSourceUrl().trim()
                : null;

        OfferRequest canonicalRequest = new OfferRequest(
                title,
                description,
                request.getDiscountType(),
                request.getDiscountValue(),
                request.getMaxDiscount(),
                request.getMinTransactionAmount(),
                request.getStartDate(),
                request.getEndDate(),
                request.getGlobalMerchantId(),
                sourceUrl);

        return offerService.addOffer(canonicalRequest);
    }
}