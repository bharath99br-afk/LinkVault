package com.linkvault.backend.bank.repository;

import com.linkvault.backend.bank.model.Bank;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {

    Optional<Bank> findByNameIgnoreCase(String name);

    Page<Bank> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable);
}