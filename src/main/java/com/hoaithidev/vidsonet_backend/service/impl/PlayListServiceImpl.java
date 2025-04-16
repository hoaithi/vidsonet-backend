package com.hoaithidev.vidsonet_backend.service.impl;

import com.hoaithidev.vidsonet_backend.dto.CategoryDTO;
import com.hoaithidev.vidsonet_backend.dto.UserDTO;
import com.hoaithidev.vidsonet_backend.dto.VideoDTO;
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
import com.hoaithidev.vidsonet_backend.service.PlayListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayListServiceImpl implements PlayListService {
    private final PlaylistRepository playlistRepository;
    private final VideoPlaylistRepository videoPlaylistRepository;
    private final VideoCategoryRepository videoCategoryRepository;

    @Override
    public List<VideoDTO> getVideosByPlayType(Long UserId, PlaylistType playlistType) {
        Playlist playlist = playlistRepository.findByUserIdAndType(UserId, playlistType)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        List<VideoPlaylist> videoPlaylists = videoPlaylistRepository.findByPlaylistId(playlist.getId());
        return videoPlaylists.stream()
                .map(vp -> mapToVideoDTO(vp.getVideo(), vp.getVideo().getUser()))
                .toList();
    }

    @Override
    @Transactional
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
