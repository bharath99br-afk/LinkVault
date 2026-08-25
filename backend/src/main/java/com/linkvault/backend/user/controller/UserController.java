package com.linkvault.backend.user.controller;

import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.user.dto.UserResponse;
import com.linkvault.backend.user.service.UserService;
import com.linkvault.backend.util.ApiResponseUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {

        UserResponse user = userService.getCurrentUser();

        return ApiResponseUtil.success(
                "User Found",
                user);
    }
}