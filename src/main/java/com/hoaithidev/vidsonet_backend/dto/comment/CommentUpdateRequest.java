package com.hoaithidev.vidsonet_backend.dto.comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequest {
    private String content;
}