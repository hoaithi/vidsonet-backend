package com.hoaithidev.vidsonet_backend.service;

import com.hoaithidev.vidsonet_backend.dto.video.VideoDTO;
import com.hoaithidev.vidsonet_backend.enums.PlaylistType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



public interface PlaylistService {

    /**
     * Remove a specific video from user's Watch Later playlist
     */
    void removeVideoFromWatchLater(Long userId, Long videoId);

    /**
     * Remove a specific video from user's History playlist
     */
    void removeVideoFromHistory(Long userId, Long videoId);



    List<VideoDTO> getVideosByPlayType(Long UserId, PlaylistType playlistType);

    @Transactional
    void clearPlaylistByType(Long userId, PlaylistType playlistType);
}