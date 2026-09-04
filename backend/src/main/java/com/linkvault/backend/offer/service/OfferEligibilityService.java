package com.linkvault.backend.offer.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linkvault.backend.card.model.Card;
import com.linkvault.backend.card.repository.CardRepository;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.offer.applicability.repository.OfferBankApplicabilityRepository;
import com.linkvault.backend.offer.applicability.repository.OfferCardApplicabilityRepository;
import com.linkvault.backend.offer.dto.OfferEligibilityRequest;
import com.linkvault.backend.offer.dto.OfferEligibilityResponse;
import com.linkvault.backend.offer.model.Offer;
import com.linkvault.backend.offer.repository.OfferRepository;
import com.linkvault.backend.security.CurrentUserService;
import com.linkvault.backend.user.model.User;

@Service
public class OfferEligibilityService {

    private final OfferRepository offerRepository;
    private final CardRepository cardRepository;
    private final OfferBankApplicabilityRepository offerBankApplicabilityRepository;
    private final OfferCardApplicabilityRepository offerCardApplicabilityRepository;
    private final CurrentUserService currentUserService;

    public OfferEligibilityService(
            OfferRepository offerRepository,
            CardRepository cardRepository,
            OfferBankApplicabilityRepository offerBankApplicabilityRepository,
            OfferCardApplicabilityRepository offerCardApplicabilityRepository,
            CurrentUserService currentUserService) {

        this.offerRepository = offerRepository;
        this.cardRepository = cardRepository;
        this.offerBankApplicabilityRepository = offerBankApplicabilityRepository;
        this.offerCardApplicabilityRepository = offerCardApplicabilityRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public OfferEligibilityResponse checkEligibility(
            Long offerId,
            OfferEligibilityRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new LinkNotFoundException("Offer Not Found"));

        Card card = cardRepository.findByIdAndUserId(
                request.getCardId(),
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Card Not Found"));

        BigDecimal transactionAmount = request.getTransactionAmount();

        /*
         * 1. Check whether the offer is currently active.
         */
        LocalDate today = LocalDate.now();

        if (today.isBefore(offer.getStartDate())
                || today.isAfter(offer.getEndDate())) {

            return ineligible(
                    offerId,
                    card.getId(),
                    "Offer is not currently active");
        }

        /*
         * 2. Check minimum transaction amount.
         */
        if (offer.getMinTransactionAmount() != null
                && transactionAmount.compareTo(
                        offer.getMinTransactionAmount()) < 0) {

            return ineligible(
                    offerId,
                    card.getId(),
                    "Minimum transaction amount is "
                            + offer.getMinTransactionAmount());
        }

        /*
         * 3. Check bank/card applicability.
         */
        boolean hasBankApplicability = !offerBankApplicabilityRepository
                .findByOfferId(offerId)
                .isEmpty();

        boolean hasCardApplicability = !offerCardApplicabilityRepository
                .findByOfferId(offerId)
                .isEmpty();

        /*
         * No bank/card restrictions means the offer
         * is eligible from the card perspective.
         */
        if (!hasBankApplicability && !hasCardApplicability) {

            return eligible(
                    offerId,
                    card.getId(),
                    "Offer is eligible for this card");
        }

        /*
         * Check bank-level applicability.
         */
        boolean bankMatches = hasBankApplicability
                && offerBankApplicabilityRepository
                        .existsByOfferIdAndBankId(
                                offerId,
                                card.getBank().getId());

        /*
         * Check exact card-level applicability.
         */
        boolean cardMatches = hasCardApplicability
                && offerCardApplicabilityRepository
                        .existsByOfferIdAndBankIdAndCardNameIgnoreCase(
                                offerId,
                                card.getBank().getId(),
                                card.getName());

        /*
         * Either a matching bank OR a matching exact card
         * is enough to make the offer eligible.
         */
        if (bankMatches || cardMatches) {

            return eligible(
                    offerId,
                    card.getId(),
                    "Offer is eligible for this card");
        }

        return ineligible(
                offerId,
                card.getId(),
                "Offer is not applicable to this card");
    }

    private OfferEligibilityResponse eligible(
            Long offerId,
            Long cardId,
            String reason) {

        return new OfferEligibilityResponse(
                offerId,
                cardId,
                true,
                reason);
    }

    private OfferEligibilityResponse ineligible(
            Long offerId,
            Long cardId,
            String reason) {

        return new OfferEligibilityResponse(
                offerId,
                cardId,
                false,
                reason);
    }
}