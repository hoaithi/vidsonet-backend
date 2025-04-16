package com.hoaithidev.vidsonet_backend.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "video_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "video_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "watch_time", nullable = false)
    private Integer currentTime; // Vị trí hiện tại (tính bằng giây)

    @Column(name = "duration", nullable = false)
    private Integer duration; // Tổng thời lượng video (tính bằng giây)

    @Column(name = "percentage", nullable = false)
    private Double percentage; // Phần trăm tiến độ xem

    @Column(name = "last_watched", nullable = false)
    private LocalDateTime lastWatched; // Thời điểm cập nhật gần nhất

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false; // Đánh dấu đã xem hết hay chưa
}