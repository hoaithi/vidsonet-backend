package com.hoaithidev.vidsonet_backend.dto.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoSearchRequest {
    private String keyword;
    private Long categoryId;
    private Long userId;
    private Boolean isPremium;
}