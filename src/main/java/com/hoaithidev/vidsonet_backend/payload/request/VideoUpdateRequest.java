package com.hoaithidev.vidsonet_backend.payload.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoUpdateRequest {
    private String title;
    private String description;
    private String thumbnailUrl;
    private List<Long> categoryIds;
    private Boolean isPremium;
}