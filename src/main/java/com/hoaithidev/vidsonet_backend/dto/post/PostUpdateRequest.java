package com.hoaithidev.vidsonet_backend.dto.post;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequest {
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    private String content;
}