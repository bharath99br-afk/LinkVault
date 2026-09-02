package com.linkvault.backend.offer.applicability.repository;

import com.linkvault.backend.offer.applicability.model.OfferCardApplicability;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferCardApplicabilityRepository
        extends JpaRepository<OfferCardApplicability, Long> {

    List<OfferCardApplicability> findByOfferId(Long offerId);

    boolean existsByOfferIdAndBankIdAndCardNameIgnoreCase(
            Long offerId,
            Long bankId,
            String cardName);
}