package com.hoaithidev.vidsonet_backend.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoSearchRequest {
    private String keyword;
    private Long categoryId;
    private Long userId;
    private Boolean isPremium;
}