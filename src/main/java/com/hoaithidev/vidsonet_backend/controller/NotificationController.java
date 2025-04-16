package com.hoaithidev.vidsonet_backend.controller;

import com.hoaithidev.vidsonet_backend.dto.NotificationDTO;
import com.hoaithidev.vidsonet_backend.payload.response.ApiResponse;
import com.hoaithidev.vidsonet_backend.service.NotificationService;
import com.hoaithidev.vidsonet_backend.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<NotificationDTO>>> getUserNotifications(
            @CurrentUser Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NotificationDTO> notifications = notificationService.getUserNotifications(userId, pageable);

        return ResponseEntity.ok(ApiResponse.<Page<NotificationDTO>>builder()
                .message("Notifications retrieved successfully")
                .data(notifications)
                .build());
    }

    @GetMapping("/unread/count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@CurrentUser Long userId) {
        long count = notificationService.getUnreadCount(userId);

        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .message("Unread notification count retrieved successfully")
                .data(count)
                .build());
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        notificationService.markAsRead(id, userId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Notification marked as read")
                .build());
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@CurrentUser Long userId) {
        notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("All notifications marked as read")
                .build());
    }
}