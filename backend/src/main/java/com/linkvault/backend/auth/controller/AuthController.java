package com.linkvault.backend.auth.controller;

import com.linkvault.backend.auth.dto.LoginRequest;
import com.linkvault.backend.auth.dto.LoginResponse;
import com.linkvault.backend.auth.service.AuthService;
import com.linkvault.backend.auth.service.LoginRateLimiter;
import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.user.dto.UserRequest;
import com.linkvault.backend.user.dto.UserResponse;
import com.linkvault.backend.user.model.User;
import com.linkvault.backend.util.ApiResponseUtil;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final LoginRateLimiter loginRateLimiter;

    public AuthController(AuthService authService, LoginRateLimiter loginRateLimiter) {
        this.authService = authService;
        this.loginRateLimiter = loginRateLimiter;
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
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest) {

        var retryAfter = loginRateLimiter.tryAcquire(servletRequest.getRemoteAddr());
        if (retryAfter.isPresent()) {
            long retryAfterSeconds = Math.max(1, (retryAfter.get().toMillis() + 999) / 1000);
            ApiResponse<LoginResponse> response = new ApiResponse<>(
                    false,
                    "Too many login attempts. Please try again later.",
                    null);

            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, Long.toString(retryAfterSeconds))
                    .body(response);
        }

        String token = authService.login(
                request.getEmail(),
                request.getPassword());

        LoginResponse response = new LoginResponse(token);

        return ApiResponseUtil.success(
                "Login Successful",
                response);
    }
}
