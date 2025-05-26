package com.hoaithidev.vidsonet_backend.service.impl;

import com.hoaithidev.vidsonet_backend.dto.categrory.CategoryDTO;
import com.hoaithidev.vidsonet_backend.dto.user.UserDTO;
import com.hoaithidev.vidsonet_backend.dto.video.*;
import com.hoaithidev.vidsonet_backend.enums.PlaylistType;
import com.hoaithidev.vidsonet_backend.enums.ReactionType;
import com.hoaithidev.vidsonet_backend.exception.ErrorCode;
import com.hoaithidev.vidsonet_backend.exception.ResourceNotFoundException;
import com.hoaithidev.vidsonet_backend.exception.VidsonetException;
import com.hoaithidev.vidsonet_backend.model.*;
import com.hoaithidev.vidsonet_backend.repository.*;
import com.hoaithidev.vidsonet_backend.service.FileStorageService;
import com.hoaithidev.vidsonet_backend.service.NotificationService;
import com.hoaithidev.vidsonet_backend.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoServiceImpl implements VideoService{

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final VideoCategoryRepository videoCategoryRepository;
    private final VideoProgressRepository videoProgressRepository;
    private final PlaylistRepository playlistRepository;
    private final VideoPlaylistRepository videoPlaylistRepository;
    private final VideoReactionRepository videoReactionRepository;
    private final MembershipRepository membershipRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;

    @Override
    public VideoUserReaction getUserReaction(long videoId, long userId) {
        Optional<VideoReaction> videoReaction = videoReactionRepository.findByUserIdAndVideoId(userId, videoId);
        if (videoReaction.isPresent()) {
            return VideoUserReaction.builder()
                    .hasReacted(true)
                    .reactionType(videoReaction.get().getReactionType())
                    .createdAt(videoReaction.get().getCreatedAt())
                    .build();
        }else{
            return VideoUserReaction.builder()
                    .hasReacted(false)
                    .build();
        }
    }


    @Override
    @Transactional
    public VideoDTO uploadVideo(Long userId, VideoUploadDTO uploadDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getChannelName() == null || user.getChannelName().isEmpty()) {
            throw new VidsonetException(ErrorCode.INVALID_REQUEST, "You need to set up your channel first");
        }

        // Kiểm tra các thông tin đầu vào
        if (uploadDTO.getVideoFile() == null || uploadDTO.getVideoFile().isEmpty()) {
            throw new VidsonetException(ErrorCode.INVALID_REQUEST, "Video file is required");
        }

        // Upload video file
        String videoPath = fileStorageService.storeFile(uploadDTO.getVideoFile(), "videos");
        String videoUrl = fileStorageService.getFileUrl(videoPath);

        // Upload thumbnail if provided
        String thumbnailUrl = null;
        if (uploadDTO.getThumbnailFile() != null && !uploadDTO.getThumbnailFile().isEmpty()) {
            String thumbnailPath = fileStorageService.storeFile(uploadDTO.getThumbnailFile(), "thumbnails");
            thumbnailUrl = fileStorageService.getFileUrl(thumbnailPath);
        }
        int duration = 0;
        try {
            String absolutePath = new File("uploads",videoPath).getAbsolutePath();
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(absolutePath);
            grabber.start();
            duration = (int)Math.round(grabber.getLengthInTime() / 1000000.0); // Chuyển từ microgiây sang giây
            grabber.stop();
        } catch (Exception e) {
            log.error("Error getting video duration: {}", e.getMessage());
        }

        // Tạo video entity
        Video video = Video.builder()
                .title(uploadDTO.getTitle())
                .description(uploadDTO.getDescription())
                .videoUrl(videoUrl)
                .thumbnailUrl(thumbnailUrl)
                .duration(duration) // Nên tính toán từ file video
                .viewCount(0L)
                .likeCount(0L)
                .dislikeCount(0L)
                .isPremium(uploadDTO.isPremium())
                .publishedAt(LocalDateTime.now())
                .user(user)
                .build();

        Video savedVideo = videoRepository.save(video);
        // Gửi thông báo đến người dùng
        notificationService.createVideoUploadNotification(savedVideo);

        // Thêm categories nếu có
        List<CategoryDTO> categoryDTOs = new ArrayList<>();
        if (uploadDTO.getCategoryIds() != null && !uploadDTO.getCategoryIds().isEmpty()) {
            for (Long categoryId : uploadDTO.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

                VideoCategory videoCategory = VideoCategory.builder()
                        .video(savedVideo)
                        .category(category)
                        .addedAt(LocalDateTime.now())
                        .build();

                videoCategoryRepository.save(videoCategory);

                categoryDTOs.add(CategoryDTO.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .build());
            }
        }


        return mapToVideoDTO(savedVideo, user, categoryDTOs, null);
    }

    @Override
    @Transactional(readOnly = true)
    /**
     * id
     * userId
     * return videoDTO
     */
    public VideoDTO getVideoById(Long id, Long userId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));

        User videoOwner = video.getUser();

        // Check if video is premium and user has access
        if (video.isPremium() && userId != null && !userId.equals(videoOwner.getId())) {
            // Check if user has an active membership
            boolean hasMembership = membershipRepository.existsByUserIdAndChannelIdAndIsActive(
                    userId, videoOwner.getId(), true);

            if (!hasMembership) {
                throw new VidsonetException(ErrorCode.PREMIUM_CONTENT);
            }
        }
        if(video.isPremium() && userId == null){
            throw new VidsonetException(ErrorCode.PREMIUM_CONTENT);
        }

        // Get categories
        List<CategoryDTO> categoryDTOs = videoCategoryRepository.findByVideoId(id).stream()
                .map(vc -> CategoryDTO.builder()
                        .id(vc.getCategory().getId())
                        .name(vc.getCategory().getName())
                        .description(vc.getCategory().getDescription())
                        .build())
                .collect(Collectors.toList());

        // Get video progress if userId is provided
        Integer currentProgress = null;
        if (userId != null) {
            Optional<VideoProgress> progressOpt = videoProgressRepository.findByUserIdAndVideoId(userId, id);
            if (progressOpt.isPresent()) {
                currentProgress = progressOpt.get().getCurrentTime();
            }
        }


        return mapToVideoDTO(video, videoOwner, categoryDTOs, currentProgress);
    }

    @Override
    @Transactional
    public VideoDTO updateVideo(Long id, VideoUpdateRequest request) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));

        User videoOwner = video.getUser();

        // Update fields if provided
        if (request.getTitle() != null) {
            video.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            video.setDescription(request.getDescription());
        }

        if (request.getThumbnailUrl() != null) {
            video.setThumbnailUrl(request.getThumbnailUrl());
        }

        if (request.getIsPremium() != null) {
            video.setPremium(request.getIsPremium());
        }

        Video updatedVideo = videoRepository.save(video);

        // Update categories if provided
        List<CategoryDTO> categoryDTOs = new ArrayList<>();
        if (request.getCategoryIds() != null) {
            // Remove existing categories
            List<VideoCategory> existingCategories = videoCategoryRepository.findByVideoId(id);
            videoCategoryRepository.deleteAll(existingCategories);

            // Add new categories
            for (Long categoryId : request.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

                VideoCategory videoCategory = VideoCategory.builder()
                        .video(updatedVideo)
                        .category(category)
                        .addedAt(LocalDateTime.now())
                        .build();

                videoCategoryRepository.save(videoCategory);

                categoryDTOs.add(CategoryDTO.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .build());
            }
        } else {
            // Get existing categories
            categoryDTOs = videoCategoryRepository.findByVideoId(id).stream()
                    .map(vc -> CategoryDTO.builder()
                            .id(vc.getCategory().getId())
                            .name(vc.getCategory().getName())
                            .description(vc.getCategory().getDescription())
                            .build())
                    .collect(Collectors.toList());
        }

        return mapToVideoDTO(updatedVideo, videoOwner, categoryDTOs, null);
    }

    @Override
    @Transactional
    public void deleteVideo(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));

        // Delete all related entities
        videoCategoryRepository.deleteByVideoId(id);
        videoProgressRepository.deleteByVideoId(id);
        videoReactionRepository.deleteByVideoId(id);
        videoPlaylistRepository.deleteByVideoId(id);

        // Delete video file and thumbnail if exists
        String videoUrl = video.getVideoUrl();
        if (videoUrl != null) {
            String videoPath = videoUrl.substring(videoUrl.lastIndexOf("/uploads/") + 9);
            fileStorageService.deleteFile(videoPath);
        }

        String thumbnailUrl = video.getThumbnailUrl();
        if (thumbnailUrl != null) {
            String thumbnailPath = thumbnailUrl.substring(thumbnailUrl.lastIndexOf("/uploads/") + 9);
            fileStorageService.deleteFile(thumbnailPath);
        }

        // Delete video entity
        videoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VideoDTO> searchVideos(VideoSearchRequest request, Pageable pageable) {
        return videoRepository.searchVideos(
                request.getKeyword(),
                request.getCategoryId(),
                request.getUserId(),
                request.getIsPremium(),
                pageable
        ).map(video -> {
            User videoOwner = video.getUser();

            List<CategoryDTO> categoryDTOs = videoCategoryRepository.findByVideoId(video.getId()).stream()
                    .map(vc -> CategoryDTO.builder()
                            .id(vc.getCategory().getId())
                            .name(vc.getCategory().getName())
                            .description(vc.getCategory().getDescription())
                            .build())
                    .collect(Collectors.toList());

            return mapToVideoDTO(video, videoOwner, categoryDTOs, null);
        });

    }

    @Override
    @Transactional
    public VideoDTO incrementView(Long id, Long userId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new VidsonetException(ErrorCode.VIDEO_NOT_FOUND, "Video not found with id: " + id));

        // Increment view count
        video.setViewCount(video.getViewCount() + 1);
        Video updatedVideo = videoRepository.save(video);

        // Add to history playlist if user is logged in
        if (userId != null) {
            addToHistoryPlaylist(id, userId);
        }

        User videoOwner = updatedVideo.getUser();

        // Get categories
        List<CategoryDTO> categoryDTOs = videoCategoryRepository.findByVideoId(id).stream()
                .map(vc -> CategoryDTO.builder()
                        .id(vc.getCategory().getId())
                        .name(vc.getCategory().getName())
                        .description(vc.getCategory().getDescription())
                        .build())
                .collect(Collectors.toList());

        return mapToVideoDTO(updatedVideo, videoOwner, categoryDTOs, null);
    }

    @Override
    @Transactional
    public void updateVideoProgress(Long videoId, Long userId, VideoProgressUpdateRequest request) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + videoId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Find existing progress or create new one
        VideoProgress progress = videoProgressRepository.findByUserIdAndVideoId(userId, videoId)
                .orElse(VideoProgress.builder()
                        .user(user)
                        .video(video)
                        .build());

        // Update fields
        progress.setCurrentTime(request.getCurrentTime());
        progress.setDuration(request.getDuration());
        progress.setLastWatched(LocalDateTime.now());

        // Calculate percentage
        double percentage = (double) request.getCurrentTime() / request.getDuration() * 100;
        progress.setPercentage(Math.min(percentage, 100.0));

        // Mark as completed if watched > 90%
        boolean isCompleted = request.getIsCompleted() != null ?
                request.getIsCompleted() : percentage >= 90.0;
        progress.setCompleted(isCompleted);

        videoProgressRepository.save(progress);
    }

    @Override
    @Transactional
    public VideoDTO likeVideo(Long id, Long userId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if user already reacted to this video
        Optional<VideoReaction> existingReaction = videoReactionRepository.findByUserIdAndVideoId(userId, id);

        if (existingReaction.isPresent()) {
            VideoReaction reaction = existingReaction.get();

            // If already liked, do nothing
            if (reaction.getReactionType() == ReactionType.LIKE) {
                videoReactionRepository.delete(reaction);
                video.setLikeCount(video.getLikeCount() - 1);
            }else{
                // If disliked, change to like and update counts
                reaction.setReactionType(ReactionType.LIKE);
                reaction.setCreatedAt(LocalDateTime.now());
                videoReactionRepository.save(reaction);

                video.setLikeCount(video.getLikeCount() + 1);
                video.setDislikeCount(video.getDislikeCount() - 1);
            }

        } else {
            // Create new like reaction
            VideoReaction reaction = VideoReaction.builder()
                    .user(user)
                    .video(video)
                    .reactionType(ReactionType.LIKE)
                    .createdAt(LocalDateTime.now())
                    .build();

            videoReactionRepository.save(reaction);

            video.setLikeCount(video.getLikeCount() + 1);
        }

        Video updatedVideo = videoRepository.save(video);

        // Get categories
        List<CategoryDTO> categoryDTOs = videoCategoryRepository.findByVideoId(id).stream()
                .map(vc -> CategoryDTO.builder()
                        .id(vc.getCategory().getId())
                        .name(vc.getCategory().getName())
                        .description(vc.getCategory().getDescription())
                        .build())
                .collect(Collectors.toList());

        return mapToVideoDTO(updatedVideo, updatedVideo.getUser(), categoryDTOs, null);
    }

    @Override
    @Transactional
    public VideoDTO dislikeVideo(Long id, Long userId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if user already reacted to this video
        Optional<VideoReaction> existingReaction = videoReactionRepository.findByUserIdAndVideoId(userId, id);

        if (existingReaction.isPresent()) {
            VideoReaction reaction = existingReaction.get();

            // If already disliked, do nothing
            if (reaction.getReactionType() == ReactionType.DISLIKE) {
                videoReactionRepository.delete(reaction);
                video.setDislikeCount(video.getDislikeCount() - 1);
            }else{
                // If liked, change to dislike and update counts
                reaction.setReactionType(ReactionType.DISLIKE);
                reaction.setCreatedAt(LocalDateTime.now());
                videoReactionRepository.save(reaction);

                video.setLikeCount(video.getLikeCount() - 1);
                video.setDislikeCount(video.getDislikeCount() + 1);
            }


        } else {
            // Create new dislike reaction
            VideoReaction reaction = VideoReaction.builder()
                    .user(user)
                    .video(video)
                    .reactionType(ReactionType.DISLIKE)
                    .createdAt(LocalDateTime.now())
                    .build();

            videoReactionRepository.save(reaction);

            video.setDislikeCount(video.getDislikeCount() + 1);
        }

        Video updatedVideo = videoRepository.save(video);

        // Get categories
        List<CategoryDTO> categoryDTOs = videoCategoryRepository.findByVideoId(id).stream()
                .map(vc -> CategoryDTO.builder()
                        .id(vc.getCategory().getId())
                        .name(vc.getCategory().getName())
                        .description(vc.getCategory().getDescription())
                        .build())
                .collect(Collectors.toList());

        return mapToVideoDTO(updatedVideo, updatedVideo.getUser(), categoryDTOs, null);
    }

    @Override
    @Transactional
    public void addToWatchLater(Long id, Long userId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));

        // Get the watch later playlist
        Playlist watchLaterPlaylist = playlistRepository.findByUserIdAndType(userId, PlaylistType.WATCH_LATER)
                .orElseThrow(() -> new ResourceNotFoundException("Watch later playlist not found"));

        // Check if video is already in the playlist
        if (!videoPlaylistRepository.existsByPlaylistIdAndVideoId(watchLaterPlaylist.getId(), id)) {
            // Add video to watch later playlist
            VideoPlaylist videoPlaylist = VideoPlaylist.builder()
                    .video(video)
                    .playlist(watchLaterPlaylist)
                    .addedAt(LocalDateTime.now())
                    .build();
            videoPlaylistRepository.save(videoPlaylist);
        }
    }

    @Override
    public Page<VideoDTO> getWatchLaterVideos(Long userId, Pageable pageable) {
        // Get the watch later playlist
        Playlist watchLaterPlaylist = playlistRepository.findByUserIdAndType(userId, PlaylistType.WATCH_LATER)
                .orElseThrow(() -> new VidsonetException(ErrorCode.PLAYLIST_NOT_FOUND, "Watch later playlist not found"));
        
        // Get videos in the watch later playlist with pagination
        Page<VideoPlaylist> videoPlaylistPage = videoPlaylistRepository.findByPlaylistId(watchLaterPlaylist.getId(), pageable);
        
        // Convert to VideoDTO
        List<VideoDTO> videoDTOs = videoPlaylistPage.getContent().stream()
                .map(vp -> {
                    Video video = vp.getVideo();
                    User videoOwner = video.getUser();
                    
                    // Get categories
                    List<CategoryDTO> categoryDTOs = videoCategoryRepository.findByVideoId(video.getId()).stream()
                            .map(vc -> CategoryDTO.builder()
                                    .id(vc.getCategory().getId())
                                    .name(vc.getCategory().getName())
                                    .description(vc.getCategory().getDescription())
                                    .build())
                            .collect(Collectors.toList());
                    
                    // Get video progress if available
                    Integer currentProgress = null;
                    Optional<VideoProgress> progressOpt = videoProgressRepository.findByUserIdAndVideoId(userId, video.getId());
                    if (progressOpt.isPresent()) {
                        currentProgress = progressOpt.get().getCurrentTime();
                    }
                    
                    return mapToVideoDTO(video, videoOwner, categoryDTOs, currentProgress);
                })
                .collect(Collectors.toList());
        
        // Return paginated result
        return new PageImpl<>(videoDTOs, pageable, videoPlaylistPage.getTotalElements());
    }


    private void addToHistoryPlaylist(Long videoId, Long userId) {
        // Get history playlist
        Playlist historyPlaylist = playlistRepository.findByUserIdAndType(userId, PlaylistType.HISTORY)
                .orElseThrow(() -> new ResourceNotFoundException("History playlist not found"));
        log.error("History playlist ID: " + historyPlaylist.getId());
        // Check if video is already in history
        Optional<VideoPlaylist> existingEntry = videoPlaylistRepository
                .findByPlaylistIdAndVideoId(historyPlaylist.getId(), videoId);

        if (existingEntry.isPresent()) {
            // Update the timestamp
            VideoPlaylist videoPlaylist = existingEntry.get();
            videoPlaylist.setAddedAt(LocalDateTime.now());
            videoPlaylistRepository.save(videoPlaylist);
        } else {
            // Add video to history playlist
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + videoId));

            VideoPlaylist videoPlaylist = VideoPlaylist.builder()
                    .video(video)
                    .playlist(historyPlaylist)
                    .addedAt(LocalDateTime.now())
                    .build();

            videoPlaylistRepository.save(videoPlaylist);
        }
    }

    private VideoDTO mapToVideoDTO(Video video, User videoOwner, List<CategoryDTO> categories, Integer currentProgress) {
        UserDTO userDTO = UserDTO.builder()
                .id(videoOwner.getId())
                .username(videoOwner.getUsername())
                .profilePicture(videoOwner.getProfilePicture())
                .channelName(videoOwner.getChannelName())
                .channelPicture(videoOwner.getChannelPicture())
                .build();

        return VideoDTO.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .videoUrl(video.getVideoUrl())
                .thumbnailUrl(video.getThumbnailUrl())
                .duration(video.getDuration())
                .viewCount(video.getViewCount())
                .likeCount(video.getLikeCount())
                .dislikeCount(video.getDislikeCount())
                .isPremium(video.isPremium())
                .publishedAt(video.getPublishedAt())
                .user(userDTO)
                .categories(categories)
                .currentProgress(currentProgress)
                .build();
    }
}