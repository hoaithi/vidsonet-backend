package com.hoaithidev.vidsonet_backend.repository;

import com.hoaithidev.vidsonet_backend.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT v FROM Video v JOIN v.videoCategories vc WHERE " +
            "(:keyword IS NULL OR LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR vc.category.id = :categoryId) AND " +
            "(:userId IS NULL OR v.user.id = :userId) AND " +
            "(:isPremium IS NULL OR v.isPremium = :isPremium)")
    Page<Video> searchVideos(String keyword, Long categoryId, Long userId, Boolean isPremium, Pageable pageable);
}



