package com.hoaithidev.vidsonet_backend.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String profilePicture;
    private LocalDateTime createdAt;
    private String channelName;
    private String channelDescription;
    private String channelPicture;
    private String bannerImage;
    private Long subscriberCount;
}