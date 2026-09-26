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

        OfferRequest canonicalRequest = new OfferRequest(
                request.getTitle(),
                request.getDescription(),
                request.getDiscountType(),
                request.getDiscountValue(),
                request.getMaxDiscount(),
                request.getMinTransactionAmount(),
                request.getStartDate(),
                request.getEndDate(),
                request.getGlobalMerchantId(),
                request.getSourceUrl());

        return offerService.addOffer(canonicalRequest);
    }
}