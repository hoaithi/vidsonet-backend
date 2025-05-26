package com.hoaithidev.vidsonet_backend.dto.video;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadRequest {
    private String title;
    private String description;
    private List<Long> categoryIds;
    private boolean isPremium;
}
