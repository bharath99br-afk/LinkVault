package com.linkvault.backend.card.controller;

import com.linkvault.backend.card.dto.CardRequest;
import com.linkvault.backend.card.dto.CardResponse;
import com.linkvault.backend.card.service.CardService;
import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.util.ApiResponseUtil;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CardResponse>>> getCards(
            @RequestParam(required = false) String name,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        PageResponse<CardResponse> cards = cardService.getCards(name, pageable);

        return ApiResponseUtil.success(
                "Cards Found",
                cards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CardResponse>> getCard(
            @PathVariable Long id) {

        CardResponse card = cardService.getCard(id);

        return ApiResponseUtil.success(
                "Card Found",
                card);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CardResponse>> addCard(
            @Valid @RequestBody CardRequest request) {

        CardResponse card = cardService.addCard(request);

        return ApiResponseUtil.created(
                "Card Created Successfully",
                card);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CardResponse>> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody CardRequest request) {

        CardResponse card = cardService.updateCard(id, request);

        return ApiResponseUtil.success(
                "Card Updated Successfully",
                card);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCard(
            @PathVariable Long id) {

        cardService.deleteCard(id);

        return ApiResponseUtil.success(
                "Card Deleted Successfully",
                null);
    }
}