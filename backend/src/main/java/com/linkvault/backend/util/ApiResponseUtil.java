package com.linkvault.backend.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.linkvault.backend.dto.ApiResponse;

public class ApiResponseUtil {

    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {

        ApiResponse<T> response = new ApiResponse<>(true, message, data);

        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {

        ApiResponse<T> response = new ApiResponse<>(true, message, data);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {

        ApiResponse<T> response = new ApiResponse<>(false, message, null);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message, T data) {

        ApiResponse<T> response = new ApiResponse<>(false, message, data);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}