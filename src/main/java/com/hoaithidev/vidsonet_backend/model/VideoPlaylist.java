package com.hoaithidev.vidsonet_backend.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "video_playlists" , uniqueConstraints = {
        @UniqueConstraint(columnNames = {"video_id", "playlist_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoPlaylist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(name = "position")
    private Integer position;

    // Thông tin tiến độ xem - chỉ có ý nghĩa khi playlist.type = HISTORY
    @Column(name = "watch_position")
    private Integer watchPosition;

    @Column(name = "last_watched")
    private LocalDateTime lastWatched;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Column(name = "percentage")
    private Double percentage;
}