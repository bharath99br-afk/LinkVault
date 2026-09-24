package com.linkvault.backend.savedoffer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.linkvault.backend.savedoffer.model.SavedOffer;
import com.linkvault.backend.user.model.User;

public interface SavedOfferRepository
        extends JpaRepository<SavedOffer, Long> {

    Optional<SavedOffer> findByUserIdAndOfferId(
            Long userId,
            Long offerId);

    boolean existsByUserIdAndOfferId(
            Long userId,
            Long offerId);

    List<SavedOffer> findByUserIdOrderByIdDesc(
            Long userId);
}