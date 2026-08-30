package com.linkvault.backend.offer.service;

import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.offer.dto.OfferRequest;
import com.linkvault.backend.offer.dto.OfferResponse;
import com.linkvault.backend.offer.model.Offer;
import com.linkvault.backend.offer.repository.OfferRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OfferService {

    private final OfferRepository repository;

    public OfferService(OfferRepository repository) {
        this.repository = repository;
    }

    public PageResponse<OfferResponse> getOffers(
            String title,
            Pageable pageable) {

        Page<Offer> page;

        if (title == null || title.isBlank()) {

            page = repository.findAllByOrderByStartDateDesc(
                    pageable);

        } else {

            page = repository.findByTitleContainingIgnoreCase(
                    title,
                    pageable);
        }

        return mapToPageResponse(page);
    }

    public OfferResponse getOffer(Long id) {

        Offer offer = repository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException("Offer Not Found"));

        return mapToResponse(offer);
    }

    public OfferResponse addOffer(OfferRequest request) {

        validateDates(
                request.getStartDate(),
                request.getEndDate());

        Offer offer = new Offer();

        offer.setTitle(request.getTitle());
        offer.setDescription(request.getDescription());
        offer.setDiscountType(request.getDiscountType());
        offer.setDiscountValue(request.getDiscountValue());
        offer.setMaxDiscount(request.getMaxDiscount());
        offer.setMinTransactionAmount(
                request.getMinTransactionAmount());
        offer.setStartDate(request.getStartDate());
        offer.setEndDate(request.getEndDate());

        Offer savedOffer = repository.save(offer);

        return mapToResponse(savedOffer);
    }

    public OfferResponse updateOffer(
            Long id,
            OfferRequest request) {

        validateDates(
                request.getStartDate(),
                request.getEndDate());

        Offer offer = repository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException("Offer Not Found"));

        offer.setTitle(request.getTitle());
        offer.setDescription(request.getDescription());
        offer.setDiscountType(request.getDiscountType());
        offer.setDiscountValue(request.getDiscountValue());
        offer.setMaxDiscount(request.getMaxDiscount());
        offer.setMinTransactionAmount(
                request.getMinTransactionAmount());
        offer.setStartDate(request.getStartDate());
        offer.setEndDate(request.getEndDate());

        Offer updatedOffer = repository.save(offer);

        return mapToResponse(updatedOffer);
    }

    public void deleteOffer(Long id) {

        Offer offer = repository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException("Offer Not Found"));

        repository.delete(offer);
    }

    private void validateDates(
            java.time.LocalDate startDate,
            java.time.LocalDate endDate) {

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date");
        }
    }

    private PageResponse<OfferResponse> mapToPageResponse(
            Page<Offer> page) {

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }

    private OfferResponse mapToResponse(Offer offer) {

        return new OfferResponse(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getDiscountType(),
                offer.getDiscountValue(),
                offer.getMaxDiscount(),
                offer.getMinTransactionAmount(),
                offer.getStartDate(),
                offer.getEndDate());
    }
}