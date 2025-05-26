package com.hoaithidev.vidsonet_backend.service.impl;

import com.hoaithidev.vidsonet_backend.dto.notification.NotificationDTO;
import com.hoaithidev.vidsonet_backend.dto.user.UserDTO;
import com.hoaithidev.vidsonet_backend.enums.EntityType;
import com.hoaithidev.vidsonet_backend.enums.NotificationType;
import com.hoaithidev.vidsonet_backend.exception.ResourceNotFoundException;
import com.hoaithidev.vidsonet_backend.model.*;
import com.hoaithidev.vidsonet_backend.repository.NotificationRepository;
import com.hoaithidev.vidsonet_backend.repository.SubscriptionRepository;
import com.hoaithidev.vidsonet_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToNotificationDTO);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        // Kiểm tra xem thông báo có thuộc về người dùng không
        if (!notification.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification not found for user");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);

        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    @Override
    @Transactional
    public void createSubscriptionNotification(User subscriber, User channel) {
        Notification notification = Notification.builder()
                .type(NotificationType.NEW_SUBSCRIBER)
                .user(channel) // Thông báo gửi đến chủ kênh
                .actor(subscriber) // Người thực hiện hành động là người đăng ký
                .content(subscriber.getUsername() + " đã đăng ký kênh của bạn")
                .entityId(subscriber.getId())
                .entityType(EntityType.USER)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void createVideoUploadNotification(Video video) {
        User videoOwner = video.getUser();

        // Lấy danh sách người đăng ký kênh này và có bật thông báo
        List<Subscription> subscriptions = subscriptionRepository.findByChannelIdAndNotificationEnabled(
                videoOwner.getId(), true);

        for (Subscription subscription : subscriptions) {
            Notification notification = Notification.builder()
                    .type(NotificationType.NEW_VIDEO)
                    .user(subscription.getUser()) // Thông báo gửi đến người đăng ký
                    .actor(videoOwner) // Người thực hiện hành động là chủ kênh
                    .content(videoOwner.getChannelName() + " đã đăng một video mới: " + video.getTitle())
                    .entityId(video.getId())
                    .entityType(EntityType.VIDEO)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void createVideoLikeNotification(Video video, User liker) {
        // Không tạo thông báo nếu người like là chủ video
        if (liker.getId().equals(video.getUser().getId())) {
            return;
        }

        Notification notification = Notification.builder()
                .type(NotificationType.VIDEO_LIKE)
                .user(video.getUser()) // Thông báo gửi đến chủ video
                .actor(liker) // Người thực hiện hành động là người like
                .content(liker.getUsername() + " đã thích video của bạn: " + video.getTitle())
                .entityId(video.getId())
                .entityType(EntityType.VIDEO)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void createCommentNotification(Comment comment) {
        // Không tạo thông báo nếu người comment là chủ video
        if (comment.getUser().getId().equals(comment.getVideo().getUser().getId())) {
            return;
        }

        Notification notification = Notification.builder()
                .type(NotificationType.VIDEO_COMMENT)
                .user(comment.getVideo().getUser()) // Thông báo gửi đến chủ video
                .actor(comment.getUser()) // Người thực hiện hành động là người comment
                .content(comment.getUser().getUsername() + " đã bình luận về video của bạn: " +
                        comment.getVideo().getTitle())
                .entityId(comment.getId())
                .entityType(EntityType.COMMENT)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        // Nếu là reply, tạo thêm thông báo cho người comment gốc
        if (comment.getParentComment() != null &&
                !comment.getUser().getId().equals(comment.getParentComment().getUser().getId())) {

            Notification replyNotification = Notification.builder()
                    .type(NotificationType.COMMENT_REPLY)
                    .user(comment.getParentComment().getUser()) // Thông báo gửi đến người comment gốc
                    .actor(comment.getUser()) // Người thực hiện hành động là người reply
                    .content(comment.getUser().getUsername() + " đã trả lời bình luận của bạn")
                    .entityId(comment.getId())
                    .entityType(EntityType.COMMENT)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(replyNotification);
        }
    }

    @Override
    @Transactional
    public void createCommentHeartNotification(Comment comment, User videoOwner) {
        // Không tạo thông báo nếu người heart là chủ comment
        if (videoOwner.getId().equals(comment.getUser().getId())) {
            return;
        }

        Notification notification = Notification.builder()
                .type(NotificationType.COMMENT_HEART)
                .user(comment.getUser()) // Thông báo gửi đến người comment
                .actor(videoOwner) // Người thực hiện hành động là chủ video
                .content(videoOwner.getChannelName() + " đã thả tim cho bình luận của bạn")
                .entityId(comment.getId())
                .entityType(EntityType.COMMENT)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void createPostNotification(Post post) {
        User postOwner = post.getUser();

        // Lấy danh sách người đăng ký kênh này và có bật thông báo
        List<Subscription> subscriptions = subscriptionRepository.findByChannelIdAndNotificationEnabled(
                postOwner.getId(), true);

        for (Subscription subscription : subscriptions) {
            Notification notification = Notification.builder()
                    .type(NotificationType.NEW_POST)
                    .user(subscription.getUser()) // Thông báo gửi đến người đăng ký
                    .actor(postOwner) // Người thực hiện hành động là chủ kênh
                    .content(postOwner.getChannelName() + " đã đăng một bài viết mới: " + post.getTitle())
                    .entityId(post.getId())
                    .entityType(EntityType.POST)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
        }
    }
    @Override
    @Transactional
    public void createPostLikeNotification(Post post, User liker) {
        // Don't create notification if the liker is the post owner
        if (liker.getId().equals(post.getUser().getId())) {
            return;
        }

        Notification notification = Notification.builder()
                .type(NotificationType.POST_LIKE)
                .user(post.getUser())  // Notification sent to post owner
                .actor(liker)  // Person who performed the action
                .content(liker.getUsername() + " liked your post: " + post.getTitle())
                .entityId(post.getId())
                .entityType(EntityType.POST)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void createPostCommentNotification(PostComment comment) {
        // Don't create notification if the commenter is the post owner
        if (comment.getUser().getId().equals(comment.getPost().getUser().getId())) {
            return;
        }

        Notification notification = Notification.builder()
                .type(NotificationType.POST_COMMENT)
                .user(comment.getPost().getUser())  // Notification sent to post owner
                .actor(comment.getUser())  // Person who commented
                .content(comment.getUser().getUsername() + " commented on your post: " + comment.getPost().getTitle())
                .entityId(comment.getId())
                .entityType(EntityType.POST_COMMENT)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        // If this is a reply, notify the parent comment owner
        if (comment.getParentComment() != null &&
                !comment.getUser().getId().equals(comment.getParentComment().getUser().getId())) {

            Notification replyNotification = Notification.builder()
                    .type(NotificationType.COMMENT_REPLY)
                    .user(comment.getParentComment().getUser())  // Notification sent to parent comment owner
                    .actor(comment.getUser())  // Person who replied
                    .content(comment.getUser().getUsername() + " replied to your comment")
                    .entityId(comment.getId())
                    .entityType(EntityType.POST_COMMENT)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(replyNotification);
        }
    }

    @Override
    @Transactional
    public void createPostCommentHeartNotification(PostComment comment, User postOwner) {
        // Don't create notification if the post owner is the comment owner
        if (postOwner.getId().equals(comment.getUser().getId())) {
            return;
        }

        Notification notification = Notification.builder()
                .type(NotificationType.POST_COMMENT_HEART)
                .user(comment.getUser())  // Notification sent to comment owner
                .actor(postOwner)  // Post owner who hearted the comment
                .content(postOwner.getChannelName() + " hearted your comment")
                .entityId(comment.getId())
                .entityType(EntityType.POST_COMMENT)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    private NotificationDTO mapToNotificationDTO(Notification notification) {
        UserDTO actorDTO = UserDTO.builder()
                .id(notification.getActor().getId())
                .username(notification.getActor().getUsername())
                .profilePicture(notification.getActor().getProfilePicture())
                .channelName(notification.getActor().getChannelName())
                .channelPicture(notification.getActor().getChannelPicture())
                .build();

        return NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.isRead())
                .entityId(notification.getEntityId())
                .entityType(notification.getEntityType())
                .actor(actorDTO)
                .build();
    }
}