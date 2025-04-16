package com.hoaithidev.vidsonet_backend.dto;

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
public class PostCommentDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long likeCount;
    private Long dislikeCount;
    private boolean isHearted;
    private LocalDateTime heartedAt;
    private UserDTO user;
    private Long postId;
    private Long parentId;
    private List<PostCommentDTO> replies;
}