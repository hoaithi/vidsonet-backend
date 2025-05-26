package com.hoaithidev.vidsonet_backend.dto.video;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadDTO {
    private String title;
    private String description;
    private List<Long> categoryIds;
    @JsonProperty("isPremium")
    private String isPremium;

    @JsonIgnore  // Để tránh lỗi khi serialize
    private MultipartFile videoFile;

    @JsonIgnore  // Để tránh lỗi khi serialize
    private MultipartFile thumbnailFile;

    public boolean isPremium() {
        return "true".equalsIgnoreCase(isPremium);
    }
}