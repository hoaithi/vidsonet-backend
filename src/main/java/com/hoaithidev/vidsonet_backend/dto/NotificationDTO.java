package com.hoaithidev.vidsonet_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hoaithidev.vidsonet_backend.enums.EntityType;
import com.hoaithidev.vidsonet_backend.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private NotificationType type;
    private LocalDateTime createdAt;
    @JsonProperty("isRead")
    private boolean isRead = false;
    private String content;
    private Long entityId;
    private EntityType entityType;
    private UserDTO user;
    private UserDTO actor;
}
