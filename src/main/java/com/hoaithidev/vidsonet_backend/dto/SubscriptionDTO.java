package com.hoaithidev.vidsonet_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    private Long id;
    private LocalDateTime subscribedAt;
    private boolean notificationEnabled = true;
    private UserDTO user;
    private UserDTO channel;
}
