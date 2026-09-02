package com.linkvault.backend.offer.applicability.repository;

import com.linkvault.backend.offer.applicability.model.OfferBankApplicability;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferBankApplicabilityRepository
        extends JpaRepository<OfferBankApplicability, Long> {

    List<OfferBankApplicability> findByOfferId(Long offerId);

    boolean existsByOfferIdAndBankId(
            Long offerId,
            Long bankId);
}