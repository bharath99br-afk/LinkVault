package com.linkvault.backend.auth.controller;

import com.linkvault.backend.auth.dto.LoginRequest;
import com.linkvault.backend.auth.dto.LoginResponse;
import com.linkvault.backend.auth.service.AuthService;
import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.user.dto.UserRequest;
import com.linkvault.backend.user.dto.UserResponse;
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
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody UserRequest request) {

        User user = authService.register(request);

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail());

        return ApiResponseUtil.created(
                "User Registered Successfully",
                response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(
                request.getEmail(),
                request.getPassword());

        LoginResponse response = new LoginResponse(token);

        return ApiResponseUtil.success(
                "Login Successful",
                response);
    }
}