package com.linkvault.backend.auth.controller;

import com.linkvault.backend.auth.service.AuthService;
import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.user.dto.UserRequest;
import com.linkvault.backend.user.model.User;
import com.linkvault.backend.util.ApiResponseUtil;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(
            @Valid @RequestBody UserRequest request) {

        User user = authService.register(request);

        return ApiResponseUtil.created(
                "User Registered Successfully",
                user);
    }
}