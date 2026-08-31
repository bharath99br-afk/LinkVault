package com.linkvault.backend.globalmerchant.repository;

import com.linkvault.backend.globalmerchant.model.GlobalMerchant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GlobalMerchantRepository extends JpaRepository<GlobalMerchant, Long> {

    Optional<GlobalMerchant> findByNameIgnoreCase(String name);

    Page<GlobalMerchant> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable);

    Optional<GlobalMerchant> findByWebsiteUrlIgnoreCase(String websiteUrl);
}