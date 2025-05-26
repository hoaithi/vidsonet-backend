package com.hoaithidev.vidsonet_backend.service;

import com.hoaithidev.vidsonet_backend.dto.video.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface VideoService {
    @Transactional
    VideoUserReaction getUserReaction(long videoId, long userId);

    VideoDTO uploadVideo(Long userId, VideoUploadDTO uploadDTO);
    VideoDTO getVideoById(Long id, Long userId);
    VideoDTO updateVideo(Long id, VideoUpdateRequest request);
    void deleteVideo(Long id);
    Page<VideoDTO> searchVideos(VideoSearchRequest request, Pageable pageable);
    VideoDTO incrementView(Long id, Long userId);
    void updateVideoProgress(Long videoId, Long userId, VideoProgressUpdateRequest request);
    VideoDTO likeVideo(Long id, Long userId);
    VideoDTO dislikeVideo(Long id, Long userId);
    void addToWatchLater(Long id, Long userId);
    Page<VideoDTO> getWatchLaterVideos(Long userId, Pageable pageable);
//
//    Page<VideoDTO> getVideos(Pageable pageable);
}