package com.linkvault.backend.deal.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linkvault.backend.card.model.Card;
import com.linkvault.backend.card.repository.CardRepository;
import com.linkvault.backend.deal.dto.BestDealRequest;
import com.linkvault.backend.deal.dto.BestDealResponse;
import com.linkvault.backend.deal.dto.DealOptionResponse;
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
    private final OfferCalculationService offerCalculationService;
    private final CurrentUserService currentUserService;

    public BestDealService(
            OfferRepository offerRepository,
            CardRepository cardRepository,
            OfferCalculationService offerCalculationService,
            CurrentUserService currentUserService) {

        this.offerRepository = offerRepository;
        this.cardRepository = cardRepository;
        this.offerCalculationService = offerCalculationService;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public BestDealResponse findBestDeal(
            BestDealRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        List<Card> cards = cardRepository.findByUserId(
                currentUser.getId(),
                org.springframework.data.domain.Pageable.unpaged())
                .getContent();

        LocalDate today = LocalDate.now();

        List<Offer> activeOffers = offerRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        today,
                        today);

        List<DealOptionResponse> eligibleDeals = new ArrayList<>();

        for (Offer offer : activeOffers) {

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
}