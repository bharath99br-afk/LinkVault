package com.linkvault.backend.card.catalog.controller;

import com.linkvault.backend.card.catalog.dto.CardProductResponse;
import com.linkvault.backend.card.catalog.service.CardProductService;
import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.util.ApiResponseUtil;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/card-products")
public class CardProductController {

    private final CardProductService cardProductService;

    public CardProductController(
            CardProductService cardProductService) {

        this.cardProductService = cardProductService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CardProductResponse>>> getCardProducts(
            @RequestParam Long bankId,
            @RequestParam(required = false) String name,
            @PageableDefault(page = 0, size = 20) Pageable pageable) {

        PageResponse<CardProductResponse> products = cardProductService.getCardProducts(
                bankId,
                name,
                pageable);

        return ApiResponseUtil.success(
                "Card Products Found",
                products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CardProductResponse>> getCardProduct(@PathVariable Long id) {

        CardProductResponse product = cardProductService.getCardProduct(id);

        return ApiResponseUtil.success(
                "Card Product Found",
                product);
    }
}