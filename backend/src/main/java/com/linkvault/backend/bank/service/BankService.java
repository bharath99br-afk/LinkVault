package com.linkvault.backend.bank.service;

import com.linkvault.backend.bank.dto.BankRequest;
import com.linkvault.backend.bank.dto.BankResponse;
import com.linkvault.backend.bank.model.Bank;
import com.linkvault.backend.bank.repository.BankRepository;
import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.exception.DuplicateResourceException;
import com.linkvault.backend.exception.LinkNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BankService {

    private final BankRepository repository;

    public BankService(BankRepository repository) {
        this.repository = repository;
    }

    public PageResponse<BankResponse> getBanks(
            String name,
            Pageable pageable) {

        Page<Bank> page;

        if (name == null || name.isBlank()) {

            page = repository.findAll(pageable);

        } else {

            page = repository.findByNameContainingIgnoreCase(
                    name,
                    pageable);
        }

        return mapToPageResponse(page);
    }

    public BankResponse getBank(Long id) {

        Bank bank = repository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException("Bank Not Found"));

        return mapToResponse(bank);
    }

    public BankResponse addBank(BankRequest request) {

        repository.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Bank with this name already exists");
                });

        Bank bank = new Bank();

        bank.setName(request.getName());

        Bank savedBank = repository.save(bank);

        return mapToResponse(savedBank);
    }

    public BankResponse updateBank(
            Long id,
            BankRequest request) {

        Bank bank = repository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException("Bank Not Found"));

        repository.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Bank with this name already exists");
                });

        bank.setName(request.getName());

        Bank updatedBank = repository.save(bank);

        return mapToResponse(updatedBank);
    }

    public void deleteBank(Long id) {

        Bank bank = repository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException("Bank Not Found"));

        repository.delete(bank);
    }

    private PageResponse<BankResponse> mapToPageResponse(
            Page<Bank> page) {

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }

    private BankResponse mapToResponse(Bank bank) {

        return new BankResponse(
                bank.getId(),
                bank.getName());
    }
}