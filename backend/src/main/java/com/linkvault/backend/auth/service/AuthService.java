package com.linkvault.backend.auth.service;

import com.linkvault.backend.exception.DuplicateResourceException;
import com.linkvault.backend.security.JwtService;
import com.linkvault.backend.user.dto.UserRequest;
import com.linkvault.backend.user.model.User;
import com.linkvault.backend.user.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(UserRequest request) {

        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email is already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    public String login(String email, String password) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password));

        return jwtService.generateToken(email);
    }
}