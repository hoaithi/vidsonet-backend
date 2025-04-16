package com.hoaithidev.vidsonet_backend.repository;

import com.hoaithidev.vidsonet_backend.model.PostCommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostCommentReactionRepository extends JpaRepository<PostCommentReaction, Long> {
    Optional<PostCommentReaction> findByUserIdAndCommentId(Long userId, Long commentId);

    void deleteByCommentId(Long commentId);
}