package com.hoaithidev.vidsonet_backend.repository;

import com.hoaithidev.vidsonet_backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findById(long id);

    List<Comment> findByVideoIdAndParentCommentIsNullOrderByCreatedAtDesc(Long videoId);

    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(Long commentId);

    List<Comment> findByVideoIdAndIsPinnedTrue(Long id);
}
