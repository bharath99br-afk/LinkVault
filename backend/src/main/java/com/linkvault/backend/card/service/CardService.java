package com.linkvault.backend.card.service;

import com.linkvault.backend.bank.model.Bank;
import com.linkvault.backend.bank.repository.BankRepository;
import com.linkvault.backend.card.dto.CardRequest;
import com.linkvault.backend.card.dto.CardResponse;
import com.linkvault.backend.card.model.Card;
import com.linkvault.backend.card.repository.CardRepository;
import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.security.CurrentUserService;
import com.linkvault.backend.user.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.linkvault.backend.card.catalog.model.CardProduct;
import com.linkvault.backend.card.catalog.repository.CardProductRepository;

@Service
public class CardService {

    private final CardRepository repository;
    private final BankRepository bankRepository;
    private final CurrentUserService currentUserService;
    private final CardProductRepository cardProductRepository;

    public CardService(
            CardRepository repository,
            BankRepository bankRepository,
            CurrentUserService currentUserService,
            CardProductRepository cardProductRepository) {

        this.repository = repository;
        this.bankRepository = bankRepository;
        this.currentUserService = currentUserService;
        this.cardProductRepository = cardProductRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<CardResponse> getCards(
            String name,
            Pageable pageable) {

        User currentUser = currentUserService.getCurrentUser();

        Page<Card> page;

        if (name == null || name.isBlank()) {

            page = repository.findByUserId(
                    currentUser.getId(),
                    pageable);

        } else {

            page = repository.findByUserIdAndNameContainingIgnoreCase(
                    currentUser.getId(),
                    name,
                    pageable);
        }

        return mapToPageResponse(page);
    }

    @Transactional(readOnly = true)
    public CardResponse getCard(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Card card = repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Card Not Found"));

        return mapToResponse(card);
    }

    @Transactional
    public CardResponse addCard(CardRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Bank bank = resolveBank(request.getBankId());
        CardProduct cardProduct = resolveCardProduct(
                request.getCardProductId(),
                bank);

        Card card = new Card();

        card.setName(request.getName());
        card.setLastFourDigits(request.getLastFourDigits());
        card.setCardType(request.getCardType());
        card.setBank(bank);
        card.setCardProduct(cardProduct);
        card.setUser(currentUser);

        Card savedCard = repository.save(card);

        return mapToResponse(savedCard);
    }

    @Transactional
    public CardResponse updateCard(
            Long id,
            CardRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Card card = repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Card Not Found"));

        Bank bank = resolveBank(request.getBankId());
        CardProduct cardProduct = resolveCardProduct(
                request.getCardProductId(),
                bank);

        card.setName(request.getName());
        card.setLastFourDigits(request.getLastFourDigits());
        card.setCardType(request.getCardType());
        card.setBank(bank);
        card.setCardProduct(cardProduct);

        Card updatedCard = repository.save(card);

        return mapToResponse(updatedCard);
    }

    @Transactional
    public void deleteCard(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Card card = repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Card Not Found"));

        repository.delete(card);
    }

    private Bank resolveBank(Long bankId) {

        return bankRepository.findById(bankId)
                .orElseThrow(() -> new LinkNotFoundException("Bank Not Found"));
    }

    private CardProduct resolveCardProduct(
            Long cardProductId,
            Bank bank) {

        if (cardProductId == null) {
            return null;
        }

        CardProduct cardProduct = cardProductRepository
                .findByIdAndActiveTrue(cardProductId)
                .orElseThrow(() -> new LinkNotFoundException(
                        "Card Product Not Found"));

        if (!cardProduct.getBank().getId().equals(bank.getId())) {
            throw new IllegalArgumentException(
                    "Card Product does not belong to selected Bank");
        }

        return cardProduct;
    }

    private PageResponse<CardResponse> mapToPageResponse(
            Page<Card> page) {

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

    private CardResponse mapToResponse(Card card) {

        Bank bank = card.getBank();
        CardProduct cardProduct = card.getCardProduct();

        return new CardResponse(
                card.getId(),
                card.getName(),
                card.getLastFourDigits(),
                card.getCardType(),
                bank != null ? bank.getId() : null,
                bank != null ? bank.getName() : null,
                cardProduct != null ? cardProduct.getId() : null,
                cardProduct != null ? cardProduct.getName() : null);
    }
}