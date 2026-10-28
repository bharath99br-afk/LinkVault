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
import com.linkvault.backend.deal.dto.BestDealNoMatchReason;
import com.linkvault.backend.deal.dto.BestDealResponse;
import com.linkvault.backend.deal.dto.DealOfferScope;
import com.linkvault.backend.deal.dto.DealOptionResponse;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.globalmerchant.model.GlobalMerchant;
import com.linkvault.backend.globalmerchant.service.GlobalMerchantMatcher;
import com.linkvault.backend.link.model.Link;
import com.linkvault.backend.link.repository.LinkRepository;
import com.linkvault.backend.merchant.model.Merchant;
import com.linkvault.backend.merchant.repository.MerchantRepository;
import com.linkvault.backend.offer.dto.OfferCalculationRequest;
import com.linkvault.backend.offer.dto.OfferCalculationResponse;
import com.linkvault.backend.offer.model.Offer;
import com.linkvault.backend.offer.repository.OfferRepository;
import com.linkvault.backend.offer.service.OfferCalculationService;
import com.linkvault.backend.product.model.Product;
import com.linkvault.backend.product.repository.ProductRepository;
import com.linkvault.backend.security.CurrentUserService;
import com.linkvault.backend.user.model.User;

@Service
public class BestDealService {

        private final OfferRepository offerRepository;
        private final CardRepository cardRepository;
        private final LinkRepository linkRepository;
        private final MerchantRepository merchantRepository;
        private final ProductRepository productRepository;
        private final GlobalMerchantMatcher globalMerchantMatcher;
        private final OfferCalculationService offerCalculationService;
        private final CurrentUserService currentUserService;

        public BestDealService(
                        OfferRepository offerRepository,
                        CardRepository cardRepository,
                        LinkRepository linkRepository,
                        MerchantRepository merchantRepository,
                        ProductRepository productRepository,
                        GlobalMerchantMatcher globalMerchantMatcher,
                        OfferCalculationService offerCalculationService,
                        CurrentUserService currentUserService) {

                this.offerRepository = offerRepository;
                this.cardRepository = cardRepository;
                this.linkRepository = linkRepository;
                this.merchantRepository = merchantRepository;
                this.productRepository = productRepository;
                this.globalMerchantMatcher = globalMerchantMatcher;
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
                        if (request.getProductId() != null) {
                                throw new IllegalArgumentException(
                                                "Specify either a saved link or a saved product, not both");
                        }

                        link = linkRepository.findByIdAndUserId(
                                        request.getLinkId(),
                                        currentUser.getId())
                                        .orElseThrow(
                                                        () -> new LinkNotFoundException("Link Not Found"));
                }

                Product product = null;

                if (request.getProductId() != null) {
                        product = productRepository.findByIdAndUserId(
                                        request.getProductId(),
                                        currentUser.getId())
                                        .orElseThrow(() -> new LinkNotFoundException("Product Not Found"));
                } else if (link != null) {
                        product = link.getProduct();
                }

                MerchantContext merchantContext = resolveMerchantContext(
                                link,
                                product,
                                request.getMerchantId(),
                                currentUser.getId());

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
                 * Merchant-specific offers are only valid candidates when the
                 * purchase has a matched merchant. Requests without a merchant
                 * can still use general offers, which apply across merchants.
                 */
                Long globalMerchantId = merchantContext.globalMerchant() == null
                                ? null
                                : merchantContext.globalMerchant().getId();

                List<Offer> candidateOffers = filterOffersForMerchant(
                                activeOffers,
                                globalMerchantId);

                DealOfferScope offerScope = globalMerchantId == null
                                ? DealOfferScope.GENERAL_ONLY
                                : DealOfferScope.MERCHANT_AND_GENERAL;

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

                                deal.setCashbackAmount(calculation.getCashbackAmount());
                                deal.setEffectivePrice(calculation.getEffectivePrice());

                                eligibleDeals.add(deal);
                        }
                }

                /*
                 * Rank by effective price, then checkout amount. If both
                 * financial outcomes are equal, use stable alphabetical
                 * ordering so equivalent options are returned consistently.
                 */
                eligibleDeals.sort(
                                Comparator.comparing(DealOptionResponse::getEffectivePrice)
                                                .thenComparing(DealOptionResponse::getFinalAmount)
                                                .thenComparing(
                                                                DealOptionResponse::getOfferTitle,
                                                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                                                .thenComparing(
                                                                DealOptionResponse::getCardName,
                                                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                                                .thenComparing(
                                                                DealOptionResponse::getOfferId,
                                                                Comparator.nullsLast(Comparator.naturalOrder()))
                                                .thenComparing(
                                                                DealOptionResponse::getCardId,
                                                                Comparator.nullsLast(Comparator.naturalOrder())));

                if (eligibleDeals.isEmpty()) {
                        BestDealNoMatchReason noDealReason = cards.isEmpty()
                                        ? BestDealNoMatchReason.NO_SAVED_CARDS
                                        : candidateOffers.isEmpty()
                                                        ? BestDealNoMatchReason.NO_ACTIVE_OFFERS
                                                        : BestDealNoMatchReason.NO_ELIGIBLE_OPTIONS;

                        return new BestDealResponse(
                                        null,
                                        List.of(),
                                        merchantContext.merchantName(),
                                        offerScope,
                                        noDealReason);
                }

                DealOptionResponse bestDeal = eligibleDeals.get(0);

                List<DealOptionResponse> alternatives = new ArrayList<>(
                                eligibleDeals.subList(
                                                1,
                                                eligibleDeals.size()));

                return new BestDealResponse(
                                bestDeal,
                                alternatives,
                                merchantContext.merchantName(),
                                offerScope);
        }

        /**
         * Filters active offers to general offers plus offers for the matched
         * merchant. A missing purchase context must not make offers for
         * unrelated stores appear eligible.
         *
         * Rules:
         *
         * 1. No matched merchant:
         * - only general offers are candidates.
         *
         * 2. A GlobalMerchant is matched:
         * - general offers are candidates.
         * - offers belonging to the same GlobalMerchant are candidates.
         * - offers belonging to another GlobalMerchant are excluded.
         */
        private List<Offer> filterOffersForMerchant(
                        List<Offer> activeOffers,
                        Long globalMerchantId) {

                return activeOffers.stream()
                                .filter(offer -> {

                                        /*
                                         * General offer.
                                         */
                                        if (offer.getGlobalMerchant() == null) {
                                                return true;
                                        }

                                        if (globalMerchantId == null) {
                                                return false;
                                        }

                                        return offer.getGlobalMerchant().getId()
                                                        .equals(globalMerchantId);
                                })
                                .toList();
        }

        /**
         * Resolves the merchant from an explicit user selection, saved
         * associations, or an exact website-host match.
         *
         * Priority:
         *
         * Explicit selection → Link merchant → Product merchant → Link URL →
         * Product URL
         */
        private MerchantContext resolveMerchantContext(
                        Link link,
                        Product product,
                        Long selectedMerchantId,
                        Long userId) {

                if (selectedMerchantId != null) {
                        Merchant selectedMerchant = merchantRepository
                                        .findByIdAndUserId(selectedMerchantId, userId)
                                        .orElseThrow(() -> new LinkNotFoundException("Merchant Not Found"));

                        return resolveUserMerchant(selectedMerchant);
                }

                if (link == null && product == null) {
                        return new MerchantContext(null, null);
                }

                String unresolvedMerchantName = null;

                if (link != null && link.getMerchant() != null) {
                        MerchantContext context = resolveUserMerchant(link.getMerchant());

                        if (context.globalMerchant() != null) {
                                return context;
                        }

                        unresolvedMerchantName = context.merchantName();
                }

                if (product != null && product.getMerchant() != null) {

                        MerchantContext context = resolveUserMerchant(product.getMerchant());

                        if (context.globalMerchant() != null) {
                                return context;
                        }

                        if (unresolvedMerchantName == null) {
                                unresolvedMerchantName = context.merchantName();
                        }
                }

                if (link != null) {
                        GlobalMerchant linkUrlMatch = globalMerchantMatcher
                                        .matchByWebsiteUrl(link.getUrl())
                                        .orElse(null);

                        if (linkUrlMatch != null) {
                                return new MerchantContext(linkUrlMatch, linkUrlMatch.getName());
                        }
                }

                if (product != null) {
                        GlobalMerchant productUrlMatch = globalMerchantMatcher
                                        .matchByWebsiteUrl(product.getWebsiteUrl())
                                        .orElse(null);

                        if (productUrlMatch != null) {
                                return new MerchantContext(productUrlMatch, productUrlMatch.getName());
                        }
                }

                return new MerchantContext(null, unresolvedMerchantName);
        }

        private MerchantContext resolveUserMerchant(Merchant merchant) {

                GlobalMerchant globalMerchant = merchant.getGlobalMerchant();

                if (globalMerchant == null) {
                        globalMerchant = globalMerchantMatcher
                                        .matchByWebsiteUrl(merchant.getWebsiteUrl())
                                        .orElse(null);
                }

                String merchantName = globalMerchant == null
                                ? merchant.getName()
                                : globalMerchant.getName();

                return new MerchantContext(globalMerchant, merchantName);
        }

        private record MerchantContext(
                        GlobalMerchant globalMerchant,
                        String merchantName) {
        }
}
