package com.linkvault.backend.product.repository;

import com.linkvault.backend.product.model.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByUserId(
            Long userId,
            Pageable pageable);

    Optional<Product> findByIdAndUserId(
            Long id,
            Long userId);

    Page<Product> findByUserIdAndNameContainingIgnoreCase(
            Long userId,
            String name,
            Pageable pageable);
}