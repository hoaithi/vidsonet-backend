package com.hoaithidev.vidsonet_backend.controller;

import com.hoaithidev.vidsonet_backend.dto.user.UserDTO;
import com.hoaithidev.vidsonet_backend.dto.user.UpdateProfileRequest;
import com.hoaithidev.vidsonet_backend.dto.user.ApiResponse;
import com.hoaithidev.vidsonet_backend.util.CurrentUser;
import com.hoaithidev.vidsonet_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .message("User retrieved successfully")
                .data(userDTO)
                .build());
    }

    @GetMapping("/{id}/channel")
    public ResponseEntity<ApiResponse<UserDTO>> getChannelByUserId(@PathVariable Long id) {
        UserDTO userDTO = userService.getChannelByUserId(id);
        return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .message("Channel retrieved successfully")
                .data(userDTO)
                .build());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@userSecurity.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @PathVariable Long id,
            @Valid @ModelAttribute UpdateProfileRequest request) {
        UserDTO updatedUser = userService.updateProfile(id, request);
        return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .message("Profile updated successfully")
                .data(updatedUser)
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(@CurrentUser Long userId) {
        UserDTO userDTO = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .message("Current user retrieved successfully")
                .data(userDTO)
                .build());
    }
    @GetMapping("/auth-test")
    public ResponseEntity<ApiResponse<Object>> testAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> authInfo = new HashMap<>();
        authInfo.put("isAuthenticated", authentication != null && authentication.isAuthenticated());

        if (authentication != null) {
            authInfo.put("principal", authentication.getPrincipal().toString());
            authInfo.put("authorities", authentication.getAuthorities().stream()
                    .map(Object::toString).collect(Collectors.toList()));
        }

        return ResponseEntity.ok(ApiResponse.builder()
                .message("Authentication info")
                .data(authInfo)
                .build());
    }
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Object>> getAllUser() {
        return ResponseEntity.ok(ApiResponse.builder()
                .message("All user")
                .data(userService.getAllUser())
                .build());
    }
}