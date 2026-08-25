package com.linkvault.backend.merchant.controller;

import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.merchant.dto.MerchantRequest;
import com.linkvault.backend.merchant.dto.MerchantResponse;
import com.linkvault.backend.merchant.model.Merchant;
import com.linkvault.backend.merchant.service.MerchantService;
import com.linkvault.backend.util.ApiResponseUtil;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

        private final MerchantService merchantService;

        public MerchantController(MerchantService merchantService) {
                this.merchantService = merchantService;
        }

        @GetMapping
        public ResponseEntity<ApiResponse<PageResponse<MerchantResponse>>> getMerchants(
                        @PageableDefault(page = 0, size = 10) Pageable pageable) {

                PageResponse<MerchantResponse> merchants = merchantService.getMerchants(pageable);

                return ApiResponseUtil.success(
                                "Merchants Found",
                                merchants);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<MerchantResponse>> getMerchant(
                        @PathVariable Long id) {

                MerchantResponse merchant = merchantService.getMerchant(id);

                return ApiResponseUtil.success(
                                "Merchant Found",
                                merchant);
        }

        @PostMapping
        public ResponseEntity<ApiResponse<MerchantResponse>> addMerchant(
                        @Valid @RequestBody MerchantRequest request) {

                MerchantResponse merchant = merchantService.addMerchant(request);

                return ApiResponseUtil.created(
                                "Merchant Created Successfully",
                                merchant);
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<MerchantResponse>> updateMerchant(
                        @PathVariable Long id,
                        @Valid @RequestBody MerchantRequest request) {

                MerchantResponse merchant = merchantService.updateMerchant(id, request);

                return ApiResponseUtil.success(
                                "Merchant Updated Successfully",
                                merchant);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Object>> deleteMerchant(
                        @PathVariable Long id) {

                merchantService.deleteMerchant(id);

                return ApiResponseUtil.success(
                                "Merchant Deleted Successfully",
                                null);
        }
}