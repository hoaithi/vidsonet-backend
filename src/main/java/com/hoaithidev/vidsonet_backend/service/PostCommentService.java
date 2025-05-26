package com.hoaithidev.vidsonet_backend.service;



import com.hoaithidev.vidsonet_backend.dto.postcomment.PostCommentDTO;
import com.hoaithidev.vidsonet_backend.dto.postcomment.PostCommentCreateRequest;
import com.hoaithidev.vidsonet_backend.dto.postcomment.PostCommentUpdateRequest;

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