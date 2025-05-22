package com.hoaithidev.vidsonet_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoDTO {
    private Long id;
    private String title;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;
    private int duration;
    private Long viewCount;
    private Long likeCount;
    private Long dislikeCount;
    @JsonProperty("isPremium")
    private boolean isPremium;
    private LocalDateTime publishedAt;
    private UserDTO user;
    private List<CategoryDTO> categories;
    private Integer currentProgress; // Chỉ có giá trị khi có userId được cung cấp
}
