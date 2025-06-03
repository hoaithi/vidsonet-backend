package com.hoaithidev.vidsonet_backend.service.impl;

import com.hoaithidev.vidsonet_backend.enums.PlaylistType;
import com.hoaithidev.vidsonet_backend.enums.UserRole;
import com.hoaithidev.vidsonet_backend.exception.DuplicateResourceException;
import com.hoaithidev.vidsonet_backend.exception.ErrorCode;
import com.hoaithidev.vidsonet_backend.exception.VidsonetException;
import com.hoaithidev.vidsonet_backend.model.Playlist;
import com.hoaithidev.vidsonet_backend.model.User;
import com.hoaithidev.vidsonet_backend.dto.user.LoginRequest;
import com.hoaithidev.vidsonet_backend.dto.user.RegisterRequest;
import com.hoaithidev.vidsonet_backend.dto.user.AuthResponse;
import com.hoaithidev.vidsonet_backend.repository.PlaylistRepository;
import com.hoaithidev.vidsonet_backend.repository.UserRepository;
import com.hoaithidev.vidsonet_backend.security.jwt.JwtTokenProvider;
import com.hoaithidev.vidsonet_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    @Override
    public void register(RegisterRequest request) {
        // Check for existing username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        // Check for existing email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Create user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        // Create default playlists
        createDefaultPlaylists(savedUser);
    }

    private void createDefaultPlaylists(User user) {
        // Watch Later playlist
        playlistRepository.save(Playlist.builder()
                .name("Watch Later")
                .type(PlaylistType.WATCH_LATER)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build());

        // Liked Videos playlist
        playlistRepository.save(Playlist.builder()
                .name("Liked Videos")
                .type(PlaylistType.LIKED_VIDEOS)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build());

        // Disliked Videos playlist
        playlistRepository.save(Playlist.builder()
                .name("Disliked Videos")
                .type(PlaylistType.DISLIKED_VIDEOS)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build());

        // History playlist
        playlistRepository.save(Playlist.builder()
                .name("History")
                .type(PlaylistType.HISTORY)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build());
    }



    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                    .orElseThrow(() -> new VidsonetException(ErrorCode.USER_NOT_FOUND));

            // Log thông tin user
            System.out.println("User found: " + user.getId() + " - " + user.getUsername());

            String accessToken = tokenProvider.generateAccessToken(user);
            String refreshToken = tokenProvider.generateRefreshToken(user);

            // Log token đã tạo
            System.out.println("Token generated for user: " + user.getId());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .username(user.getUsername())
                    .build();
        } catch (VidsonetException e) {
           throw e; // Ném lại ngoại lệ nếu có
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new VidsonetException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = tokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new VidsonetException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = tokenProvider.generateAccessToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)  // Reuse the same refresh token
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
}