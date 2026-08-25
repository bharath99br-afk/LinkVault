package com.linkvault.backend.user.service;

import com.linkvault.backend.security.CurrentUserService;
import com.linkvault.backend.user.dto.UserResponse;
import com.linkvault.backend.user.model.User;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final CurrentUserService currentUserService;

    public UserService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    public UserResponse getCurrentUser() {

        User user = currentUserService.getCurrentUser();

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail());
    }
}