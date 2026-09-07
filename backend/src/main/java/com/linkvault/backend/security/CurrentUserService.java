package com.linkvault.backend.security;

import com.linkvault.backend.user.model.User;
import com.linkvault.backend.user.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {

            throw new IllegalStateException(
                    "No authenticated user found");
        }

        String email = authentication.getName();

        return userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Authenticated user not found"));
    }
}