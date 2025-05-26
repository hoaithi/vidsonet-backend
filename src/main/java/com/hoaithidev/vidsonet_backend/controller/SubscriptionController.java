package com.hoaithidev.vidsonet_backend.controller;


import com.hoaithidev.vidsonet_backend.dto.user.SubscriptionDTO;
import com.hoaithidev.vidsonet_backend.dto.user.ApiResponse;
import com.hoaithidev.vidsonet_backend.service.SubscriptionService;
import com.hoaithidev.vidsonet_backend.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{channelId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<SubscriptionDTO>> subscribe(
            @PathVariable Long channelId,
            @RequestParam(defaultValue = "true") boolean notificationEnabled,
            @CurrentUser Long userId) {

        SubscriptionDTO subscription = subscriptionService.subscribe(userId, channelId, notificationEnabled);

        return ResponseEntity.ok(ApiResponse.<SubscriptionDTO>builder()
                .message("Subscribed successfully")
                .data(subscription)
                .build());
    }

    @DeleteMapping("/{channelId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> unsubscribe(
            @PathVariable Long channelId,
            @CurrentUser Long userId) {

        subscriptionService.unsubscribe(userId, channelId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Unsubscribed successfully")
                .build());
    }

    @PutMapping("/{channelId}/notifications")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> toggleNotifications(
            @PathVariable Long channelId,
            @RequestParam boolean enabled,
            @CurrentUser Long userId) {

        subscriptionService.toggleNotification(userId, channelId, enabled);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Notification settings updated")
                .build());
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<SubscriptionDTO>>> getUserSubscriptions(
            @CurrentUser Long userId) {

        List<SubscriptionDTO> subscriptions = subscriptionService.getUserSubscriptions(userId);

        return ResponseEntity.ok(ApiResponse.<List<SubscriptionDTO>>builder()
                .message("User subscriptions retrieved successfully")
                .data(subscriptions)
                .build());
    }

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<ApiResponse<List<SubscriptionDTO>>> getChannelSubscribers(
            @PathVariable Long channelId) {

        List<SubscriptionDTO> subscribers = subscriptionService.getChannelSubscribers(channelId);

        return ResponseEntity.ok(ApiResponse.<List<SubscriptionDTO>>builder()
                .message("Channel subscribers retrieved successfully")
                .data(subscribers)
                .build());
    }

    @GetMapping("/channel/{channelId}/count")
    public ResponseEntity<ApiResponse<Long>> getSubscriberCount(
            @PathVariable Long channelId) {

        long count = subscriptionService.getSubscriberCount(channelId);

        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .message("Subscriber count retrieved successfully")
                .data(count)
                .build());
    }

    @GetMapping("/check")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Boolean>> checkSubscription(
            @RequestParam Long channelId,
            @CurrentUser Long userId) {

        boolean isSubscribed = subscriptionService.isSubscribed(userId, channelId);
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .message("Subscription status checked")
                .data(isSubscribed)
                .build());
    }
}