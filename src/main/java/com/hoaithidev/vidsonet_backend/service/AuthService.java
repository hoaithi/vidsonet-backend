package com.hoaithidev.vidsonet_backend.service;

import com.hoaithidev.vidsonet_backend.payload.request.LoginRequest;
import com.hoaithidev.vidsonet_backend.payload.request.RegisterRequest;
import com.hoaithidev.vidsonet_backend.payload.response.AuthResponse;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {
    @Transactional
    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken);
}
