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
        private final OfferCardMatcher offerCardMatcher;

        public OfferEligibilityService(
                        OfferRepository offerRepository,
                        CardRepository cardRepository,
                        OfferBankApplicabilityRepository offerBankApplicabilityRepository,
                        OfferCardApplicabilityRepository offerCardApplicabilityRepository,
                        CurrentUserService currentUserService,
                        OfferCardMatcher offerCardMatcher) {

                this.offerRepository = offerRepository;
                this.cardRepository = cardRepository;
                this.offerBankApplicabilityRepository = offerBankApplicabilityRepository;
                this.offerCardApplicabilityRepository = offerCardApplicabilityRepository;
                this.currentUserService = currentUserService;
                this.offerCardMatcher = offerCardMatcher;
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
                var bankApplicabilities = offerBankApplicabilityRepository.findByOfferId(offerId);

                var cardApplicabilities = offerCardApplicabilityRepository.findByOfferId(offerId);

                boolean cardMatches = offerCardMatcher.matches(
                                card,
                                bankApplicabilities,
                                cardApplicabilities);

                if (cardMatches) {
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