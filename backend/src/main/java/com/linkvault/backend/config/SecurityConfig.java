package com.linkvault.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.linkvault.backend.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);

        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {

        http
                // JWT-based APIs do not use browser sessions.
                .csrf(csrf -> csrf.disable())

                // Disable default browser authentication mechanisms.
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // Explicitly enable CORS processing in Spring Security.
                .cors(cors -> {
                })

                // Do not create or maintain HTTP sessions.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // Public authentication endpoints.
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login")
                        .permitAll()

                        // Everything else requires authentication.
                        .anyRequest()
                        .authenticated())

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                authenticationEntryPoint())
                        .accessDeniedHandler(
                                accessDeniedHandler()))

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {

        return (request, response, authException) -> {

            response.setStatus(
                    HttpStatus.UNAUTHORIZED.value());

            response.setContentType("application/json");

            response.getWriter().write("""
                    {
                        "success": false,
                        "message": "Authentication required",
                        "data": null
                    }
                    """);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {

        return (request, response, accessDeniedException) -> {

            response.setStatus(
                    HttpStatus.FORBIDDEN.value());

            response.setContentType("application/json");

            response.getWriter().write("""
                    {
                        "success": false,
                        "message": "Access denied",
                        "data": null
                    }
                    """);
        };
    }
}