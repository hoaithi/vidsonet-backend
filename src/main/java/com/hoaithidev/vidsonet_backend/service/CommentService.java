package com.hoaithidev.vidsonet_backend.service;

import com.hoaithidev.vidsonet_backend.dto.CommentDTO;
import com.hoaithidev.vidsonet_backend.payload.request.CommentCreateRequest;
import com.hoaithidev.vidsonet_backend.payload.request.CommentUpdateRequest;

import java.util.List;

public interface CommentService {
    CommentDTO createComment(Long userId, CommentCreateRequest request);
    CommentDTO updateComment(Long commentId, Long userId, CommentUpdateRequest request);
    void deleteComment(Long commentId, Long userId);
    CommentDTO getCommentById(Long commentId);
    List<CommentDTO> getCommentsByVideoId(Long videoId);
    List<CommentDTO> getRepliesByCommentId(Long commentId);
    CommentDTO heartComment(Long commentId, Long userId);
    CommentDTO unhearComment(Long commentId, Long userId);
    CommentDTO pinComment(Long commentId, Long userId);
    CommentDTO unpinComment(Long commentId, Long userId);
}