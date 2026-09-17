package com.linkvault.backend.card.catalog.repository;

import com.linkvault.backend.card.catalog.model.CardProduct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardProductRepository
        extends JpaRepository<CardProduct, Long> {

    Page<CardProduct> findByBankIdAndActiveTrue(
            Long bankId,
            Pageable pageable);

    Page<CardProduct> findByBankIdAndNameContainingIgnoreCaseAndActiveTrue(
            Long bankId,
            String name,
            Pageable pageable);

    Optional<CardProduct> findByIdAndActiveTrue(Long id);

    Optional<CardProduct> findByBankIdAndNameIgnoreCase(
            Long bankId,
            String name);
}