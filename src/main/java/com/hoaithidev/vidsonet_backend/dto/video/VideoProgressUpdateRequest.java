package com.hoaithidev.vidsonet_backend.dto.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoProgressUpdateRequest {
    private Integer currentTime;
    private Integer duration;
    private Boolean isCompleted;
}