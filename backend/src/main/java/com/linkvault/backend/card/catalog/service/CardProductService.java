package com.linkvault.backend.card.catalog.service;

import com.linkvault.backend.bank.model.Bank;
import com.linkvault.backend.card.catalog.dto.CardProductResponse;
import com.linkvault.backend.card.catalog.model.CardProduct;
import com.linkvault.backend.card.catalog.repository.CardProductRepository;
import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.exception.LinkNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardProductService {

    private final CardProductRepository repository;

    public CardProductService(CardProductRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PageResponse<CardProductResponse> getCardProducts(
            Long bankId,
            String name,
            Pageable pageable) {

        Page<CardProduct> page;

        if (bankId == null) {
            throw new IllegalArgumentException(
                    "Bank ID is required");
        }

        if (name == null || name.isBlank()) {
            page = repository.findByBankIdAndActiveTrue(
                    bankId,
                    pageable);
        } else {
            page = repository
                    .findByBankIdAndNameContainingIgnoreCaseAndActiveTrue(
                            bankId,
                            name,
                            pageable);
        }

        return mapToPageResponse(page);
    }

    @Transactional(readOnly = true)
    public CardProductResponse getCardProduct(Long id) {

        CardProduct cardProduct = repository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() -> new LinkNotFoundException(
                        "Card Product Not Found"));

        return mapToResponse(cardProduct);
    }

    private PageResponse<CardProductResponse> mapToPageResponse(
            Page<CardProduct> page) {

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

    private CardProductResponse mapToResponse(
            CardProduct cardProduct) {

        Bank bank = cardProduct.getBank();

        return new CardProductResponse(
                cardProduct.getId(),
                bank.getId(),
                bank.getName(),
                cardProduct.getName(),
                cardProduct.getCardType(),
                cardProduct.isActive());
    }
}