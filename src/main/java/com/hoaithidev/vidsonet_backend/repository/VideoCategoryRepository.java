package com.hoaithidev.vidsonet_backend.repository;

import com.hoaithidev.vidsonet_backend.model.VideoCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoCategoryRepository extends JpaRepository<VideoCategory, Long> {
    List<VideoCategory> findByVideoId(Long id);

    void deleteByVideoId(Long id);
}
