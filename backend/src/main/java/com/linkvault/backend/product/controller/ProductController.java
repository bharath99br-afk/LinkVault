package com.linkvault.backend.product.controller;

import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.product.dto.ProductRequest;
import com.linkvault.backend.product.dto.ProductResponse;
import com.linkvault.backend.product.service.ProductService;
import com.linkvault.backend.util.ApiResponseUtil;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getProducts(
            @RequestParam(required = false) String name,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        PageResponse<ProductResponse> products;

        if (name == null || name.isBlank()) {
            products = productService.getProducts(pageable);
        } else {
            products = productService.searchProducts(name, pageable);
        }

        return ApiResponseUtil.success(
                "Products Found",
                products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @PathVariable Long id) {

        ProductResponse product = productService.getProduct(id);

        return ApiResponseUtil.success(
                "Product Found",
                product);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(
            @Valid @RequestBody ProductRequest request) {

        ProductResponse product = productService.addProduct(request);

        return ApiResponseUtil.created(
                "Product Created Successfully",
                product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        ProductResponse product = productService.updateProduct(id, request);

        return ApiResponseUtil.success(
                "Product Updated Successfully",
                product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ApiResponseUtil.success(
                "Product Deleted Successfully",
                null);
    }
}