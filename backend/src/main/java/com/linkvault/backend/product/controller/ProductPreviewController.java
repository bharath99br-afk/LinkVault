package com.linkvault.backend.product.controller;

import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.product.dto.ProductPreviewRequest;
import com.linkvault.backend.product.dto.ProductPreviewResponse;
import com.linkvault.backend.product.service.ProductPreviewService;
import com.linkvault.backend.util.ApiResponseUtil;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductPreviewController {

    private final ProductPreviewService productPreviewService;

    public ProductPreviewController(
            ProductPreviewService productPreviewService) {

        this.productPreviewService = productPreviewService;
    }

    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<ProductPreviewResponse>> previewProduct(
            @Valid @RequestBody ProductPreviewRequest request) {

        ProductPreviewResponse preview = productPreviewService.preview(request.url());

        return ApiResponseUtil.success(
                "Product Preview Generated",
                preview);
    }
}