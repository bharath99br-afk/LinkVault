package com.linkvault.backend.deal.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linkvault.backend.card.model.Card;
import com.linkvault.backend.card.repository.CardRepository;
import com.linkvault.backend.deal.dto.BestDealRequest;
import com.linkvault.backend.deal.dto.BestDealResponse;
import com.linkvault.backend.deal.dto.DealOptionResponse;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.link.model.Link;
import com.linkvault.backend.link.repository.LinkRepository;
import com.linkvault.backend.offer.dto.OfferCalculationRequest;
import com.linkvault.backend.offer.dto.OfferCalculationResponse;
import com.linkvault.backend.offer.model.Offer;
import com.linkvault.backend.offer.repository.OfferRepository;
import com.linkvault.backend.offer.service.OfferCalculationService;
import com.linkvault.backend.security.CurrentUserService;
import com.linkvault.backend.user.model.User;

@Service
public class BestDealService {

    private final OfferRepository offerRepository;
    private final CardRepository cardRepository;
    private final LinkRepository linkRepository;
    private final OfferCalculationService offerCalculationService;
    private final CurrentUserService currentUserService;

    public BestDealService(
            OfferRepository offerRepository,
            CardRepository cardRepository,
            LinkRepository linkRepository,
            OfferCalculationService offerCalculationService,
            CurrentUserService currentUserService) {

        this.offerRepository = offerRepository;
        this.cardRepository = cardRepository;
        this.linkRepository = linkRepository;
        this.offerCalculationService = offerCalculationService;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public BestDealResponse findBestDeal(BestDealRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        /*
         * Step 7:
         * Resolve the Link only when linkId is supplied.
         *
         * Ownership is enforced directly at repository level so a user
         * cannot use another user's Link ID.
         */
        Link link = null;

        if (request.getLinkId() != null) {
            link = linkRepository.findByIdAndUserId(
                    request.getLinkId(),
                    currentUser.getId())
                    .orElseThrow(
                            () -> new LinkNotFoundException("Link Not Found"));
        }

        /*
         * Get all cards belonging to the authenticated user.
         */
        List<Card> cards = cardRepository.findByUserId(
                currentUser.getId(),
                Pageable.unpaged())
                .getContent();

        /*
         * Only active offers can participate in the deal calculation.
         */
        LocalDate today = LocalDate.now();

        List<Offer> activeOffers = offerRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        today,
                        today);

        /*
         * If this request is Link-aware, filter offers according to the
         * GlobalMerchant associated with the purchase.
         *
         * If linkId is absent, preserve the existing Step 6 behaviour
         * and evaluate all active offers.
         */
        List<Offer> candidateOffers = filterOffersForLink(
                activeOffers,
                link);

        List<DealOptionResponse> eligibleDeals = new ArrayList<>();

        /*
         * Evaluate every candidate offer against every card owned by
         * the authenticated user.
         *
         * Eligibility and calculation remain delegated to the existing
         * services. We do not duplicate that business logic here.
         */
        for (Offer offer : candidateOffers) {

            for (Card card : cards) {

                OfferCalculationRequest calculationRequest = new OfferCalculationRequest();

                calculationRequest.setCardId(card.getId());
                calculationRequest.setTransactionAmount(
                        request.getTransactionAmount());

                OfferCalculationResponse calculation = offerCalculationService.calculate(
                        offer.getId(),
                        calculationRequest);

                if (!calculation.isEligible()) {
                    continue;
                }

                DealOptionResponse deal = new DealOptionResponse(
                        offer.getId(),
                        offer.getTitle(),
                        card.getId(),
                        card.getName(),
                        card.getBank().getId(),
                        card.getBank().getName(),
                        request.getTransactionAmount(),
                        calculation.getDiscountAmount(),
                        calculation.getFinalAmount(),
                        calculation.getSavingsPercentage());

                eligibleDeals.add(deal);
            }
        }

        /*
         * Lower final amount = better deal.
         */
        eligibleDeals.sort(
                Comparator.comparing(
                        DealOptionResponse::getFinalAmount));

        if (eligibleDeals.isEmpty()) {
            return new BestDealResponse(
                    null,
                    List.of());
        }

        DealOptionResponse bestDeal = eligibleDeals.get(0);

        List<DealOptionResponse> alternatives = new ArrayList<>(
                eligibleDeals.subList(
                        1,
                        eligibleDeals.size()));

        return new BestDealResponse(
                bestDeal,
                alternatives);
    }

    /**
     * Filters active offers according to the merchant associated
     * with the saved Link.
     *
     * Rules:
     *
     * 1. No Link supplied:
     * - preserve existing Step 6 behaviour
     * - all active offers remain candidates.
     *
     * 2. Link has no merchant context:
     * - only general offers are candidates.
     *
     * 3. Link has a GlobalMerchant:
     * - general offers are candidates.
     * - offers belonging to the same GlobalMerchant are candidates.
     * - offers belonging to another GlobalMerchant are excluded.
     */
    private List<Offer> filterOffersForLink(
            List<Offer> activeOffers,
            Link link) {

        if (link == null) {
            return activeOffers;
        }

        Long globalMerchantId = resolveGlobalMerchantId(link);

        return activeOffers.stream()
                .filter(offer -> {

                    /*
                     * General offer.
                     */
                    if (offer.getGlobalMerchant() == null) {
                        return true;
                    }

                    /*
                     * Link has no merchant context, therefore a
                     * merchant-specific offer cannot be matched.
                     */
                    if (globalMerchantId == null) {
                        return false;
                    }

                    /*
                     * Merchant-specific offer must match the
                     * Link's GlobalMerchant.
                     */
                    return offer.getGlobalMerchant().getId()
                            .equals(globalMerchantId);
                })
                .toList();
    }

    /**
     * Determines the GlobalMerchant associated with the purchase.
     *
     * Priority:
     *
     * Link Merchant
     * ↓
     * Product Merchant
     * ↓
     * No merchant context
     */
    private Long resolveGlobalMerchantId(Link link) {

        if (link.getMerchant() != null
                && link.getMerchant().getGlobalMerchant() != null) {

            return link.getMerchant()
                    .getGlobalMerchant()
                    .getId();
        }

        if (link.getProduct() != null
                && link.getProduct().getMerchant() != null
                && link.getProduct()
                        .getMerchant()
                        .getGlobalMerchant() != null) {

            return link.getProduct()
                    .getMerchant()
                    .getGlobalMerchant()
                    .getId();
        }

        return null;
    }
}