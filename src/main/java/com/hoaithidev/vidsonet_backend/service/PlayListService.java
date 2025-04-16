package com.hoaithidev.vidsonet_backend.service;

import com.hoaithidev.vidsonet_backend.dto.VideoDTO;
import com.hoaithidev.vidsonet_backend.enums.PlaylistType;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PlayListService {

    List<VideoDTO> getVideosByPlayType(Long UserId, PlaylistType playlistType);

    void clearPlaylistByType(Long userId, PlaylistType playlistType);
}
