package com.linkvault.backend.savedoffer.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linkvault.backend.exception.DuplicateResourceException;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.offer.model.Offer;
import com.linkvault.backend.offer.repository.OfferRepository;
import com.linkvault.backend.savedoffer.dto.SavedOfferResponse;
import com.linkvault.backend.savedoffer.model.SavedOffer;
import com.linkvault.backend.savedoffer.repository.SavedOfferRepository;
import com.linkvault.backend.security.CurrentUserService;
import com.linkvault.backend.user.model.User;

@Service
public class SavedOfferService {

    private final SavedOfferRepository savedOfferRepository;
    private final OfferRepository offerRepository;
    private final CurrentUserService currentUserService;

    public SavedOfferService(
            SavedOfferRepository savedOfferRepository,
            OfferRepository offerRepository,
            CurrentUserService currentUserService) {

        this.savedOfferRepository = savedOfferRepository;
        this.offerRepository = offerRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public SavedOfferResponse saveOffer(Long offerId) {

        User currentUser = currentUserService.getCurrentUser();

        if (savedOfferRepository.existsByUserIdAndOfferId(
                currentUser.getId(),
                offerId)) {

            throw new DuplicateResourceException(
                    "Offer is already saved");
        }

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new LinkNotFoundException(
                        "Offer Not Found"));

        SavedOffer savedOffer = new SavedOffer();

        savedOffer.setUser(currentUser);
        savedOffer.setOffer(offer);

        SavedOffer saved = savedOfferRepository.save(savedOffer);

        return mapToResponse(saved);
    }

    @Transactional
    public void unsaveOffer(Long offerId) {

        User currentUser = currentUserService.getCurrentUser();

        SavedOffer savedOffer = savedOfferRepository
                .findByUserIdAndOfferId(
                        currentUser.getId(),
                        offerId)
                .orElseThrow(() -> new LinkNotFoundException(
                        "Saved Offer Not Found"));

        savedOfferRepository.delete(savedOffer);
    }

    @Transactional(readOnly = true)
    public List<SavedOfferResponse> getSavedOffers() {

        User currentUser = currentUserService.getCurrentUser();

        return savedOfferRepository
                .findByUserIdOrderByIdDesc(
                        currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SavedOfferResponse getSavedOffer(
            Long offerId) {

        User currentUser = currentUserService.getCurrentUser();

        SavedOffer savedOffer = savedOfferRepository
                .findByUserIdAndOfferId(
                        currentUser.getId(),
                        offerId)
                .orElseThrow(() -> new LinkNotFoundException(
                        "Saved Offer Not Found"));

        return mapToResponse(savedOffer);
    }

    private SavedOfferResponse mapToResponse(
            SavedOffer savedOffer) {

        Offer offer = savedOffer.getOffer();

        return new SavedOfferResponse(
                savedOffer.getId(),
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getDiscountType(),
                offer.getDiscountValue(),
                offer.getMaxDiscount(),
                offer.getMinTransactionAmount(),
                offer.getStartDate(),
                offer.getEndDate(),
                offer.getGlobalMerchant() != null
                        ? offer.getGlobalMerchant().getId()
                        : null,
                offer.getGlobalMerchant() != null
                        ? offer.getGlobalMerchant().getName()
                        : null);
    }
}