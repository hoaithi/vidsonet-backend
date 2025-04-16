package com.hoaithidev.vidsonet_backend.controller;


import com.hoaithidev.vidsonet_backend.dto.PostDTO;
import com.hoaithidev.vidsonet_backend.payload.request.PostCreateRequest;
import com.hoaithidev.vidsonet_backend.payload.request.PostUpdateRequest;
import com.hoaithidev.vidsonet_backend.payload.response.ApiResponse;
import com.hoaithidev.vidsonet_backend.service.PostService;
import com.hoaithidev.vidsonet_backend.util.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PostDTO>> createPost(
            @Valid @RequestBody PostCreateRequest request,
            @CurrentUser Long userId) {

        PostDTO post = postService.createPost(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PostDTO>builder()
                        .message("Post created successfully")
                        .data(post)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDTO>> getPostById(@PathVariable Long id) {
        PostDTO post = postService.getPostById(id);

        return ResponseEntity.ok(ApiResponse.<PostDTO>builder()
                .message("Post retrieved successfully")
                .data(post)
                .build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<PostDTO>>> getPostsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostDTO> posts = postService.getPostsByUserId(userId, pageable);

        return ResponseEntity.ok(ApiResponse.<Page<PostDTO>>builder()
                .message("Posts retrieved successfully")
                .data(posts)
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PostDTO>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateRequest request,
            @CurrentUser Long userId) {

        PostDTO post = postService.updatePost(id, userId, request);

        return ResponseEntity.ok(ApiResponse.<PostDTO>builder()
                .message("Post updated successfully")
                .data(post)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        postService.deletePost(id, userId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Post deleted successfully")
                .build());
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PostDTO>> likePost(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        PostDTO post = postService.likePost(id, userId);

        return ResponseEntity.ok(ApiResponse.<PostDTO>builder()
                .message("Post liked successfully")
                .data(post)
                .build());
    }

    @PostMapping("/{id}/dislike")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PostDTO>> dislikePost(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        PostDTO post = postService.dislikePost(id, userId);

        return ResponseEntity.ok(ApiResponse.<PostDTO>builder()
                .message("Post disliked successfully")
                .data(post)
                .build());
    }
}