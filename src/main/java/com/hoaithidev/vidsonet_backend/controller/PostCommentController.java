package com.hoaithidev.vidsonet_backend.controller;

import com.hoaithidev.vidsonet_backend.dto.postcomment.PostCommentDTO;
import com.hoaithidev.vidsonet_backend.dto.postcomment.PostCommentCreateRequest;
import com.hoaithidev.vidsonet_backend.dto.postcomment.PostCommentUpdateRequest;
import com.hoaithidev.vidsonet_backend.dto.user.ApiResponse;
import com.hoaithidev.vidsonet_backend.service.PostCommentService;
import com.hoaithidev.vidsonet_backend.util.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post-comments")
@RequiredArgsConstructor
public class PostCommentController {

    private final PostCommentService postCommentService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PostCommentDTO>> createComment(
            @Valid @RequestBody PostCommentCreateRequest request,
            @CurrentUser Long userId) {

        PostCommentDTO comment = postCommentService.createComment(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PostCommentDTO>builder()
                        .message("Comment created successfully")
                        .data(comment)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostCommentDTO>> getCommentById(@PathVariable Long id) {
        PostCommentDTO comment = postCommentService.getCommentById(id);

        return ResponseEntity.ok(ApiResponse.<PostCommentDTO>builder()
                .message("Comment retrieved successfully")
                .data(comment)
                .build());
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<List<PostCommentDTO>>> getCommentsByPostId(@PathVariable Long postId) {
        List<PostCommentDTO> comments = postCommentService.getCommentsByPostId(postId);

        return ResponseEntity.ok(ApiResponse.<List<PostCommentDTO>>builder()
                .message("Comments retrieved successfully")
                .data(comments)
                .build());
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<ApiResponse<List<PostCommentDTO>>> getRepliesByCommentId(@PathVariable Long id) {
        List<PostCommentDTO> replies = postCommentService.getRepliesByCommentId(id);

        return ResponseEntity.ok(ApiResponse.<List<PostCommentDTO>>builder()
                .message("Replies retrieved successfully")
                .data(replies)
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PostCommentDTO>> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody PostCommentUpdateRequest request,
            @CurrentUser Long userId) {

        PostCommentDTO comment = postCommentService.updateComment(id, userId, request);

        return ResponseEntity.ok(ApiResponse.<PostCommentDTO>builder()
                .message("Comment updated successfully")
                .data(comment)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        postCommentService.deleteComment(id, userId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Comment deleted successfully")
                .build());
    }

    @PostMapping("/{id}/heart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PostCommentDTO>> heartComment(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        PostCommentDTO comment = postCommentService.heartComment(id, userId);

        return ResponseEntity.ok(ApiResponse.<PostCommentDTO>builder()
                .message("Comment hearted successfully")
                .data(comment)
                .build());
    }

    @DeleteMapping("/{id}/heart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PostCommentDTO>> unhearComment(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        PostCommentDTO comment = postCommentService.unhearComment(id, userId);

        return ResponseEntity.ok(ApiResponse.<PostCommentDTO>builder()
                .message("Comment unhearted successfully")
                .data(comment)
                .build());
    }
}