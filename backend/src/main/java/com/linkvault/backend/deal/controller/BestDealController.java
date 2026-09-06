package com.linkvault.backend.deal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.util.ApiResponseUtil;
import com.linkvault.backend.deal.dto.BestDealRequest;
import com.linkvault.backend.deal.dto.BestDealResponse;
import com.linkvault.backend.deal.service.BestDealService;

@RestController
@RequestMapping("/api/deals")
public class BestDealController {

    private final BestDealService bestDealService;

    public BestDealController(
            BestDealService bestDealService) {

        this.bestDealService = bestDealService;
    }

    @PostMapping("/best")
    public ResponseEntity<ApiResponse<BestDealResponse>> findBestDeal(
            @Valid @RequestBody BestDealRequest request) {

        BestDealResponse response = bestDealService.findBestDeal(request);

        String message = response.getBestDeal() != null
                ? "Best Deal Found"
                : "No Eligible Deal Found";

        return ApiResponseUtil.success(
                message,
                response);
    }
}