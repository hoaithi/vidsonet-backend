package com.hoaithidev.vidsonet_backend.dto.comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {
    private String content;
    private Long videoId;
    private Long parentId;
}