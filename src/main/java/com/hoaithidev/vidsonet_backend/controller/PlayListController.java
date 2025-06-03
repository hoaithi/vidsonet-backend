package com.hoaithidev.vidsonet_backend.controller;

import com.hoaithidev.vidsonet_backend.dto.video.VideoDTO;
import com.hoaithidev.vidsonet_backend.enums.PlaylistType;
import com.hoaithidev.vidsonet_backend.dto.user.ApiResponse;
import com.hoaithidev.vidsonet_backend.service.PlaylistService;
import com.hoaithidev.vidsonet_backend.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlayListController {

    private final PlaylistService playListService;

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

    @DeleteMapping("/watch-later/{videoId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWatchLater(
            @PathVariable Long videoId,
            @CurrentUser Long userId) {

        playListService.removeVideoFromWatchLater(userId, videoId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Video removed from Watch Later successfully")
                .build());
    }

    /**
     * Remove video from History playlist
     */
    @DeleteMapping("/history/{videoId}")
    public ResponseEntity<ApiResponse<Void>> removeFromHistory(
            @PathVariable Long videoId,
            @CurrentUser Long userId) {

        playListService.removeVideoFromHistory(userId, videoId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Video removed from History successfully")
                .build());
    }

}
