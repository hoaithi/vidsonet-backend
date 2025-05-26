package com.hoaithidev.vidsonet_backend.service;

import com.hoaithidev.vidsonet_backend.dto.user.LoginRequest;
import com.hoaithidev.vidsonet_backend.dto.user.RegisterRequest;
import com.hoaithidev.vidsonet_backend.dto.user.AuthResponse;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {
    @Transactional
    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken);
}
