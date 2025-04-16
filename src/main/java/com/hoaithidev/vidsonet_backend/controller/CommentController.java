package com.hoaithidev.vidsonet_backend.controller;

import com.hoaithidev.vidsonet_backend.dto.CommentDTO;
import com.hoaithidev.vidsonet_backend.payload.request.CommentCreateRequest;
import com.hoaithidev.vidsonet_backend.payload.request.CommentUpdateRequest;
import com.hoaithidev.vidsonet_backend.payload.response.ApiResponse;
import com.hoaithidev.vidsonet_backend.service.CommentService;
import com.hoaithidev.vidsonet_backend.util.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentDTO>> getCommentById(@PathVariable Long id) {
        CommentDTO comment = commentService.getCommentById(id);

        return ResponseEntity.ok(ApiResponse.<CommentDTO>builder()
                .message("Comment retrieved successfully")
                .data(comment)
                .build());
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getCommentReplies(@PathVariable Long id) {
        List<CommentDTO> replies = commentService.getRepliesByCommentId(id);

        return ResponseEntity.ok(ApiResponse.<List<CommentDTO>>builder()
                .message("Replies retrieved successfully")
                .data(replies)
                .build());
    }

    @PostMapping("/{id}/replies")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CommentDTO>> replyToComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentCreateRequest request,
            @CurrentUser Long userId) {

        // Set the parent id from the path
        log.error("Request: {}", request.toString());
        log.error("User ID: {}", userId);
        log.error("Parent ID: {}", id);
        request.setParentId(id);

        CommentDTO reply = commentService.createComment(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CommentDTO>builder()
                        .message("Reply added successfully")
                        .data(reply)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CommentDTO>> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentUpdateRequest request,
            @CurrentUser Long userId) {

        CommentDTO updatedComment = commentService.updateComment(id, userId, request);

        return ResponseEntity.ok(ApiResponse.<CommentDTO>builder()
                .message("Comment updated successfully")
                .data(updatedComment)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        commentService.deleteComment(id, userId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Comment deleted successfully")
                .build());
    }

    @PostMapping("/{id}/heart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CommentDTO>> heartComment(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        CommentDTO heartedComment = commentService.heartComment(id, userId);

        return ResponseEntity.ok(ApiResponse.<CommentDTO>builder()
                .message("Comment hearted successfully")
                .data(heartedComment)
                .build());
    }

    @DeleteMapping("/{id}/heart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CommentDTO>> unhearComment(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        CommentDTO unhearted = commentService.unhearComment(id, userId);

        return ResponseEntity.ok(ApiResponse.<CommentDTO>builder()
                .message("Comment unhearted successfully")
                .data(unhearted)
                .build());
    }

    @PostMapping("/{id}/pin")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CommentDTO>> pinComment(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        CommentDTO pinnedComment = commentService.pinComment(id, userId);

        return ResponseEntity.ok(ApiResponse.<CommentDTO>builder()
                .message("Comment pinned successfully")
                .data(pinnedComment)
                .build());
    }

    @DeleteMapping("/{id}/pin")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CommentDTO>> unpinComment(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        CommentDTO unpinnedComment = commentService.unpinComment(id, userId);

        return ResponseEntity.ok(ApiResponse.<CommentDTO>builder()
                .message("Comment unpinned successfully")
                .data(unpinnedComment)
                .build());
    }
}