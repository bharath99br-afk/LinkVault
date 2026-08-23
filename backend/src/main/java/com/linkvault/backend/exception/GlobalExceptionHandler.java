package com.linkvault.backend.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.linkvault.backend.common.dto.ApiResponse;
import com.linkvault.backend.util.ApiResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(
                                                error.getField(),
                                                error.getDefaultMessage()));

                return ApiResponseUtil.badRequest(
                                "Validation Failed",
                                errors);
        }

        @ExceptionHandler(LinkNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleLinkNotFoundException(
                        LinkNotFoundException ex) {

                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                ex.getMessage(),
                                null);

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        @ExceptionHandler(HandlerMethodValidationException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodValidationException(
                        HandlerMethodValidationException ex) {

                Map<String, String> errors = new HashMap<>();
                ex.getParameterValidationResults().forEach(result -> {
                        String parameterName = result.getMethodParameter().getParameterName();
                        result.getResolvableErrors().forEach(error -> {
                                String message = error.getDefaultMessage();
                                errors.put(parameterName, message);
                        });
                });

                return ApiResponseUtil.badRequest(
                                "Validation Failed",
                                errors);
        }

        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ApiResponse<Object>> handleDuplicateResource(
                        DuplicateResourceException exception) {

                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                exception.getMessage(),
                                null);

                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
}