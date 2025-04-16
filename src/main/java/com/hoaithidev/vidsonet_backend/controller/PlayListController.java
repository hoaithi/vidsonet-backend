package com.hoaithidev.vidsonet_backend.controller;

import com.hoaithidev.vidsonet_backend.dto.VideoDTO;
import com.hoaithidev.vidsonet_backend.enums.PlaylistType;
import com.hoaithidev.vidsonet_backend.payload.response.ApiResponse;
import com.hoaithidev.vidsonet_backend.service.PlayListService;
import com.hoaithidev.vidsonet_backend.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlayListController {

    private final PlayListService playListService;

    @GetMapping("/watch-later")
    public ResponseEntity<ApiResponse<List<VideoDTO>>> getWatchLaterVideos(@CurrentUser Long userId) {
        List<VideoDTO> videos = playListService.getVideosByPlayType(userId, PlaylistType.WATCH_LATER);
        return ResponseEntity.ok(ApiResponse.<List<VideoDTO>>builder()
                .message("Get watch later videos successfully")
                .data(videos)
                .build());
    }
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<VideoDTO>>> getHistoryVideos(@CurrentUser Long userId) {
        List<VideoDTO> videos = playListService.getVideosByPlayType(userId, PlaylistType.HISTORY);
        return ResponseEntity.ok(ApiResponse.<List<VideoDTO>>builder()
                .message("Get history videos successfully")
                .data(videos)
                .build());
    }
    @DeleteMapping("/history/clear")
    public ResponseEntity<ApiResponse<Void>> clearHistory(@CurrentUser Long userId) {
        playListService.clearPlaylistByType(userId, PlaylistType.HISTORY);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Clear history successfully")
                .build());
    }

}
