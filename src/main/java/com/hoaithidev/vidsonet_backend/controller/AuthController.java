package com.hoaithidev.vidsonet_backend.controller;

import com.hoaithidev.vidsonet_backend.payload.request.LoginRequest;
import com.hoaithidev.vidsonet_backend.payload.request.RegisterRequest;
import com.hoaithidev.vidsonet_backend.payload.response.ApiResponse;
import com.hoaithidev.vidsonet_backend.payload.response.AuthResponse;
import com.hoaithidev.vidsonet_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Void>builder()
                        .message("User registered successfully")
                        .build());
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        log.error("Login successful: {}", authResponse);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .message("Login successful")
                .data(authResponse)
                .build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestParam String refreshToken) {
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .message("Token refreshed successfully")
                .data(authResponse)
                .build());
    }
}
