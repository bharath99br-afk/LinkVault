package com.linkvault.backend.globalmerchant.controller;

import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.globalmerchant.dto.GlobalMerchantRequest;
import com.linkvault.backend.globalmerchant.dto.GlobalMerchantResponse;
import com.linkvault.backend.globalmerchant.service.GlobalMerchantService;
import com.linkvault.backend.util.ApiResponseUtil;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/global-merchants")
public class GlobalMerchantController {

    private final GlobalMerchantService globalMerchantService;

    public GlobalMerchantController(
            GlobalMerchantService globalMerchantService) {

        this.globalMerchantService = globalMerchantService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<GlobalMerchantResponse>>> getGlobalMerchants(
            @RequestParam(required = false) String name,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        PageResponse<GlobalMerchantResponse> merchants = globalMerchantService.getGlobalMerchants(
                name,
                pageable);

        return ApiResponseUtil.success(
                "Global Merchants Found",
                merchants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GlobalMerchantResponse>> getGlobalMerchant(
            @PathVariable Long id) {

        GlobalMerchantResponse merchant = globalMerchantService.getGlobalMerchant(id);

        return ApiResponseUtil.success(
                "Global Merchant Found",
                merchant);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GlobalMerchantResponse>> addGlobalMerchant(
            @Valid @RequestBody GlobalMerchantRequest request) {

        GlobalMerchantResponse merchant = globalMerchantService.addGlobalMerchant(request);

        return ApiResponseUtil.created(
                "Global Merchant Created Successfully",
                merchant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GlobalMerchantResponse>> updateGlobalMerchant(
            @PathVariable Long id,
            @Valid @RequestBody GlobalMerchantRequest request) {

        GlobalMerchantResponse merchant = globalMerchantService.updateGlobalMerchant(
                id,
                request);

        return ApiResponseUtil.success(
                "Global Merchant Updated Successfully",
                merchant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteGlobalMerchant(
            @PathVariable Long id) {

        globalMerchantService.deleteGlobalMerchant(id);

        return ApiResponseUtil.success(
                "Global Merchant Deleted Successfully",
                null);
    }
}