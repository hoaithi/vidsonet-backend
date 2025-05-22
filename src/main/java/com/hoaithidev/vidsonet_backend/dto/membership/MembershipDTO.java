package com.hoaithidev.vidsonet_backend.dto.membership;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hoaithidev.vidsonet_backend.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipDTO {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @JsonProperty("isActive")
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private MembershipTierDTO tier;
    private UserDTO subscriber;
    private UserDTO channel;
}