package com.hoaithidev.vidsonet_backend.service;



import com.hoaithidev.vidsonet_backend.dto.PostCommentDTO;
import com.hoaithidev.vidsonet_backend.payload.request.PostCommentCreateRequest;
import com.hoaithidev.vidsonet_backend.payload.request.PostCommentUpdateRequest;

import java.util.List;

public interface PostCommentService {
    PostCommentDTO createComment(Long userId, PostCommentCreateRequest request);
    PostCommentDTO updateComment(Long id, Long userId, PostCommentUpdateRequest request);
    void deleteComment(Long id, Long userId);
    PostCommentDTO getCommentById(Long id);
    List<PostCommentDTO> getCommentsByPostId(Long postId);
    List<PostCommentDTO> getRepliesByCommentId(Long commentId);
    PostCommentDTO heartComment(Long id, Long userId);
    PostCommentDTO unhearComment(Long id, Long userId);
}