package com.hoaithidev.vidsonet_backend.service;

import com.hoaithidev.vidsonet_backend.dto.notification.NotificationDTO;
import com.hoaithidev.vidsonet_backend.model.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    Page<NotificationDTO> getUserNotifications(Long userId, Pageable pageable);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    long getUnreadCount(Long userId);

    void createSubscriptionNotification(User subscriber, User channel);

    void createVideoUploadNotification(Video video);

    void createVideoLikeNotification(Video video, User liker);

    void createCommentNotification(Comment comment);

    void createCommentHeartNotification(Comment comment, User videoOwner);

    void createPostNotification(Post post);

    void createPostLikeNotification(Post post, User liker);

    void createPostCommentNotification(PostComment comment);

    void createPostCommentHeartNotification(PostComment comment, User postOwner);
}