package com.linkvault.backend.bank.controller;

import com.linkvault.backend.bank.dto.BankRequest;
import com.linkvault.backend.bank.dto.BankResponse;
import com.linkvault.backend.bank.service.BankService;
import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.util.ApiResponseUtil;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/banks")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BankResponse>>> getBanks(
            @RequestParam(required = false) String name,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        PageResponse<BankResponse> banks = bankService.getBanks(name, pageable);

        return ApiResponseUtil.success(
                "Banks Found",
                banks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BankResponse>> getBank(
            @PathVariable Long id) {

        BankResponse bank = bankService.getBank(id);

        return ApiResponseUtil.success(
                "Bank Found",
                bank);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BankResponse>> addBank(
            @Valid @RequestBody BankRequest request) {

        BankResponse bank = bankService.addBank(request);

        return ApiResponseUtil.created(
                "Bank Created Successfully",
                bank);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BankResponse>> updateBank(
            @PathVariable Long id,
            @Valid @RequestBody BankRequest request) {

        BankResponse bank = bankService.updateBank(id, request);

        return ApiResponseUtil.success(
                "Bank Updated Successfully",
                bank);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteBank(
            @PathVariable Long id) {

        bankService.deleteBank(id);

        return ApiResponseUtil.success(
                "Bank Deleted Successfully",
                null);
    }
}