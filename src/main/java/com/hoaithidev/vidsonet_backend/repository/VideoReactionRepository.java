package com.hoaithidev.vidsonet_backend.repository;

import com.hoaithidev.vidsonet_backend.model.VideoReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoReactionRepository extends JpaRepository<VideoReaction, Long> {
    void deleteByVideoId(Long id);

    Optional<VideoReaction> findByUserIdAndVideoId(Long userId, Long id);
}
