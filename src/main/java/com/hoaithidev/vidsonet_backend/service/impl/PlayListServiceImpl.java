package com.hoaithidev.vidsonet_backend.service.impl;

import com.hoaithidev.vidsonet_backend.dto.categrory.CategoryDTO;
import com.hoaithidev.vidsonet_backend.dto.user.UserDTO;
import com.hoaithidev.vidsonet_backend.dto.video.VideoDTO;
import com.hoaithidev.vidsonet_backend.enums.PlaylistType;
import com.hoaithidev.vidsonet_backend.exception.ErrorCode;
import com.hoaithidev.vidsonet_backend.exception.ResourceNotFoundException;
import com.hoaithidev.vidsonet_backend.exception.VidsonetException;
import com.hoaithidev.vidsonet_backend.model.Playlist;
import com.hoaithidev.vidsonet_backend.model.User;
import com.hoaithidev.vidsonet_backend.model.Video;
import com.hoaithidev.vidsonet_backend.model.VideoPlaylist;
import com.hoaithidev.vidsonet_backend.repository.PlaylistRepository;
import com.hoaithidev.vidsonet_backend.repository.VideoCategoryRepository;
import com.hoaithidev.vidsonet_backend.repository.VideoPlaylistRepository;
import com.hoaithidev.vidsonet_backend.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayListServiceImpl implements PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final VideoPlaylistRepository videoPlaylistRepository;
    private final VideoCategoryRepository videoCategoryRepository;

    @Override
    @Transactional
    public void removeVideoFromWatchLater(Long userId, Long videoId) {
        // Get user's Watch Later playlist
        Playlist watchLaterPlaylist = playlistRepository.findByUserIdAndType(userId, PlaylistType.WATCH_LATER)
                .orElseThrow(() -> new ResourceNotFoundException("Watch Later playlist not found"));

        // Find the video in the playlist
        Optional<VideoPlaylist> videoPlaylistOpt = videoPlaylistRepository
                .findByPlaylistIdAndVideoId(watchLaterPlaylist.getId(), videoId);

        if (videoPlaylistOpt.isEmpty()) {
            throw new VidsonetException(ErrorCode.VIDEO_NOT_IN_PLAYLIST, "Video not found in Watch Later playlist");
        }

        // Remove the video from the playlist
        videoPlaylistRepository.delete(videoPlaylistOpt.get());
    }

    @Override
    @Transactional
    public void removeVideoFromHistory(Long userId, Long videoId) {
        // Get user's History playlist
        Playlist historyPlaylist = playlistRepository.findByUserIdAndType(userId, PlaylistType.HISTORY)
                .orElseThrow(() -> new ResourceNotFoundException("History playlist not found"));

        // Find the video in the playlist
        Optional<VideoPlaylist> videoPlaylistOpt = videoPlaylistRepository
                .findByPlaylistIdAndVideoId(historyPlaylist.getId(), videoId);

        if (videoPlaylistOpt.isEmpty()) {
            throw new VidsonetException(ErrorCode.VIDEO_NOT_IN_PLAYLIST, "Video not found in History playlist");
        }

        // Remove the video from the playlist
        videoPlaylistRepository.delete(videoPlaylistOpt.get());
    }

    @Override
    public List<VideoDTO> getVideosByPlayType(Long UserId, PlaylistType playlistType) {
        Playlist playlist = playlistRepository.findByUserIdAndType(UserId, playlistType)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        List<VideoPlaylist> videoPlaylists = videoPlaylistRepository.findByPlaylistId(playlist.getId());
        return videoPlaylists.stream()
                .map(vp -> mapToVideoDTO(vp.getVideo(), vp.getVideo().getUser()))
                .toList();
    }

    @Transactional
    @Override
    public void clearPlaylistByType(Long userId, PlaylistType playlistType) {
        Playlist playlist = playlistRepository.findByUserIdAndType(userId, playlistType)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));
        videoPlaylistRepository.deleteAllByPlaylistId(playlist.getId());
    }


    private VideoDTO mapToVideoDTO(Video video, User videoOwner) {
        UserDTO userDTO = UserDTO.builder()
                .id(videoOwner.getId())
                .username(videoOwner.getUsername())
                .channelName(videoOwner.getChannelName())
                .profilePicture(videoOwner.getProfilePicture())
                .channelPicture(videoOwner.getChannelPicture())
                .build();
        List<CategoryDTO> categoryDTOS = videoCategoryRepository.findByVideoId(video.getId())
                .stream()
                .map(videoCategory -> CategoryDTO.builder()
                        .id(videoCategory.getCategory().getId())
                        .name(videoCategory.getCategory().getName())
                        .description(videoCategory.getCategory().getDescription())
                        .build())
                .toList();
        return VideoDTO.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .thumbnailUrl(video.getThumbnailUrl())
                .publishedAt(video.getPublishedAt())
                .viewCount(video.getViewCount())
                .likeCount(video.getLikeCount())
                .dislikeCount(video.getDislikeCount())
                .duration(video.getDuration())
                .videoUrl(video.getVideoUrl())
                .user(userDTO)
                .categories(categoryDTOS)
                .build();

    }
}
