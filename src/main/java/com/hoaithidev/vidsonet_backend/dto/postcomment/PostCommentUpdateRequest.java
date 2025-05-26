package com.hoaithidev.vidsonet_backend.dto.postcomment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentUpdateRequest {
    @NotBlank(message = "Content is required")
    private String content;
}