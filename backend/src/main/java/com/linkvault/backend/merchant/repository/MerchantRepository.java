package com.linkvault.backend.merchant.repository;

import com.linkvault.backend.merchant.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findByNameIgnoreCase(String name);
}