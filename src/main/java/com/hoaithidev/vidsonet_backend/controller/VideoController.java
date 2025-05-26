package com.hoaithidev.vidsonet_backend.controller;

import com.hoaithidev.vidsonet_backend.dto.comment.CommentCreateRequest;
import com.hoaithidev.vidsonet_backend.dto.comment.CommentDTO;
import com.hoaithidev.vidsonet_backend.dto.video.*;
import com.hoaithidev.vidsonet_backend.dto.user.ApiResponse;
import com.hoaithidev.vidsonet_backend.service.CommentService;
import com.hoaithidev.vidsonet_backend.service.VideoService;
import com.hoaithidev.vidsonet_backend.util.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

    private final VideoService videoService;
    private final CommentService commentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<VideoDTO>> uploadVideo(
            @ModelAttribute VideoUploadDTO uploadDTO,
            @CurrentUser Long userId) {
        log.info("Uploading video");
        log.info("UploadDTO: {}", uploadDTO.getIsPremium());

        VideoDTO uploadedVideo = videoService.uploadVideo(userId, uploadDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<VideoDTO>builder()
                        .message("Video uploaded successfully")
                        .data(uploadedVideo)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VideoDTO>> getVideoById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {

        VideoDTO video = videoService.getVideoById(id, userId);

        return ResponseEntity.ok(ApiResponse.<VideoDTO>builder()
                .message("Video retrieved successfully")
                .data(video)
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("@videoSecurity.isVideoOwner(#id)")
    public ResponseEntity<ApiResponse<VideoDTO>> updateVideo(
            @PathVariable Long id,
            @Valid @RequestBody VideoUpdateRequest request) {

        VideoDTO updatedVideo = videoService.updateVideo(id, request);

        return ResponseEntity.ok(ApiResponse.<VideoDTO>builder()
                .message("Video updated successfully")
                .data(updatedVideo)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@videoSecurity.isVideoOwner(#id)")
    public ResponseEntity<ApiResponse<Void>> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Video deleted successfully")
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<VideoDTO>>> searchVideos(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Boolean isPremium,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        VideoSearchRequest searchRequest = new VideoSearchRequest(keyword, categoryId, userId, isPremium);

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<VideoDTO> videos = videoService.searchVideos(searchRequest, pageable);

        return ResponseEntity.ok(ApiResponse.<Page<VideoDTO>>builder()
                .message("Videos retrieved successfully")
                .data(videos)
                .build());
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<VideoDTO>> incrementView(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {

        VideoDTO video = videoService.incrementView(id, userId);

        return ResponseEntity.ok(ApiResponse.<VideoDTO>builder()
                .message("View count incremented")
                .data(video)
                .build());
    }

    @PostMapping("/{id}/progress")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> updateProgress(
            @PathVariable Long id,
            @Valid @RequestBody VideoProgressUpdateRequest request,
            @CurrentUser Long userId) {

        videoService.updateVideoProgress(id, userId, request);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Video progress updated")
                .build());
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<VideoDTO>> likeVideo(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        VideoDTO video = videoService.likeVideo(id, userId);

        return ResponseEntity.ok(ApiResponse.<VideoDTO>builder()
                .message("Video liked successfully")
                .data(video)
                .build());
    }

    @PostMapping("/{id}/dislike")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<VideoDTO>> dislikeVideo(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        VideoDTO video = videoService.dislikeVideo(id, userId);

        return ResponseEntity.ok(ApiResponse.<VideoDTO>builder()
                .message("Video disliked successfully")
                .data(video)
                .build());
    }

    @PostMapping("/{id}/watch-later")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> addToWatchLater(
            @PathVariable Long id,
            @CurrentUser Long userId) {

        videoService.addToWatchLater(id, userId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Video added to watch later")
                .build());
    }


    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getVideoComments(@PathVariable Long id) {
        List<CommentDTO> comments = commentService.getCommentsByVideoId(id);

        return ResponseEntity.ok(ApiResponse.<List<CommentDTO>>builder()
                .message("Comments retrieved successfully")
                .data(comments)
                .build());
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<CommentDTO>> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentCreateRequest request,
            @CurrentUser Long userId) {

        // Set the video id from the path
        request.setVideoId(id);

        CommentDTO comment = commentService.createComment(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CommentDTO>builder()
                        .message("Comment added successfully")
                        .data(comment)
                        .build());
    }

    @GetMapping("/watch-later")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<VideoDTO>>> getWatchLaterVideos(
            @CurrentUser Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "addedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<VideoDTO> videos = videoService.getWatchLaterVideos(userId, pageable);

        return ResponseEntity.ok(ApiResponse.<Page<VideoDTO>>builder()
                .message("Watch later videos retrieved successfully")
                .data(videos)
                .build());
    }

    @GetMapping("/{id}/reaction")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<VideoUserReaction>> getVideoUserReaction(
            @PathVariable long id,
            @CurrentUser Long userId
    ){
        VideoUserReaction videoUserReaction = videoService.getUserReaction(id,userId);
        return ResponseEntity.ok(ApiResponse.<VideoUserReaction>builder()
                .message("Video user reaction retrieved successfully")
                .data(videoUserReaction)
                .build());
    }

}
