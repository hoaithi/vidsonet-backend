package com.hoaithidev.vidsonet_backend.repository;

import com.hoaithidev.vidsonet_backend.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    long countByPostId(Long postId);

    void deleteByPostId(Long postId);

    List<PostComment> findByPostIdAndParentCommentIsNullOrderByCreatedAtDesc(Long postId);

    List<PostComment> findByParentCommentIdOrderByCreatedAtAsc(Long commentId);
}