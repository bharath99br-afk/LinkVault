package com.linkvault.backend.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.dashboard.dto.DashboardSummaryResponse;
import com.linkvault.backend.dashboard.service.DashboardService;
import com.linkvault.backend.util.ApiResponseUtil;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {

        DashboardSummaryResponse response = dashboardService.getSummary();

        return ApiResponseUtil.success(
                "Dashboard Summary Found",
                response);
    }
}