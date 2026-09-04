package com.linkvault.backend.offer.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linkvault.backend.offer.dto.OfferCalculationRequest;
import com.linkvault.backend.offer.dto.OfferCalculationResponse;
import com.linkvault.backend.offer.dto.OfferEligibilityRequest;
import com.linkvault.backend.offer.dto.OfferEligibilityResponse;
import com.linkvault.backend.offer.model.DiscountType;
import com.linkvault.backend.offer.model.Offer;
import com.linkvault.backend.offer.repository.OfferRepository;
import com.linkvault.backend.exception.LinkNotFoundException;

@Service
public class OfferCalculationService {

    private final OfferRepository offerRepository;
    private final OfferEligibilityService offerEligibilityService;

    public OfferCalculationService(
            OfferRepository offerRepository,
            OfferEligibilityService offerEligibilityService) {

        this.offerRepository = offerRepository;
        this.offerEligibilityService = offerEligibilityService;
    }

    @Transactional(readOnly = true)
    public OfferCalculationResponse calculate(
            Long offerId,
            OfferCalculationRequest request) {

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new LinkNotFoundException("Offer Not Found"));

        OfferEligibilityRequest eligibilityRequest = new OfferEligibilityRequest(
                request.getCardId(),
                request.getTransactionAmount());

        OfferEligibilityResponse eligibility = offerEligibilityService.checkEligibility(
                offerId,
                eligibilityRequest);

        if (!eligibility.isEligible()) {

            return new OfferCalculationResponse(
                    offerId,
                    request.getCardId(),
                    false,
                    eligibility.getReason(),
                    request.getTransactionAmount(),
                    null,
                    null,
                    null);
        }

        BigDecimal transactionAmount = request.getTransactionAmount();

        BigDecimal discountAmount = calculateDiscount(
                offer,
                transactionAmount);

        BigDecimal finalAmount = transactionAmount
                .subtract(discountAmount)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal savingsPercentage = discountAmount
                .divide(
                        transactionAmount,
                        6,
                        RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        return new OfferCalculationResponse(
                offerId,
                request.getCardId(),
                true,
                "Deal calculated successfully",
                transactionAmount.setScale(2, RoundingMode.HALF_UP),
                discountAmount,
                finalAmount,
                savingsPercentage);
    }

    private BigDecimal calculateDiscount(
            Offer offer,
            BigDecimal transactionAmount) {

        BigDecimal discountAmount;

        if (offer.getDiscountType() == DiscountType.PERCENTAGE) {

            discountAmount = transactionAmount
                    .multiply(offer.getDiscountValue())
                    .divide(
                            BigDecimal.valueOf(100),
                            2,
                            RoundingMode.HALF_UP);

        } else if (offer.getDiscountType() == DiscountType.FLAT) {

            discountAmount = offer.getDiscountValue()
                    .setScale(2, RoundingMode.HALF_UP);

        } else {

            throw new IllegalArgumentException(
                    "Unsupported discount type");
        }

        if (offer.getMaxDiscount() != null
                && discountAmount.compareTo(
                        offer.getMaxDiscount()) > 0) {

            discountAmount = offer.getMaxDiscount();
        }

        if (discountAmount.compareTo(transactionAmount) > 0) {

            discountAmount = transactionAmount;
        }

        return discountAmount
                .setScale(2, RoundingMode.HALF_UP);
    }
}