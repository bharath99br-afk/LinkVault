package com.linkvault.backend.offer.repository;

import com.linkvault.backend.offer.model.Offer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    Page<Offer> findAllByOrderByStartDateDesc(
            Pageable pageable);

    Page<Offer> findByTitleContainingIgnoreCase(
            String title,
            Pageable pageable);
}