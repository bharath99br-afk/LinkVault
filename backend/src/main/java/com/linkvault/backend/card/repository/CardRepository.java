package com.linkvault.backend.card.repository;

import com.linkvault.backend.card.model.Card;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findByUserId(
            Long userId,
            Pageable pageable);

    Optional<Card> findByIdAndUserId(
            Long id,
            Long userId);

    Page<Card> findByUserIdAndNameContainingIgnoreCase(
            Long userId,
            String name,
            Pageable pageable);
}