package com.hoaithidev.vidsonet_backend.repository;

import com.hoaithidev.vidsonet_backend.model.VideoPlaylist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoPlaylistRepository extends JpaRepository<VideoPlaylist, Long> {
    void deleteByVideoId(Long id);

    Optional<VideoPlaylist> findByPlaylistIdAndVideoId(Long id, Long videoId);

    boolean existsByPlaylistIdAndVideoId(Long id, Long id1);
    List<VideoPlaylist> findByPlaylistId(Long id);

    Page<VideoPlaylist> findByPlaylistId(Long id, Pageable pageable);

    void deleteAllByPlaylistId(Long id);
}
