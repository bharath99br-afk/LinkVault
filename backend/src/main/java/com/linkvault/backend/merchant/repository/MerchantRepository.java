package com.linkvault.backend.merchant.repository;

import com.linkvault.backend.merchant.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findByNameIgnoreCase(String name);

    Optional<Merchant> findByNameIgnoreCaseAndUserId(
            String name,
            Long userId);

    Optional<Merchant> findByIdAndUserId(
            Long id,
            Long userId);

    Page<Merchant> findByUserId(Long userId, Pageable pageable);
}