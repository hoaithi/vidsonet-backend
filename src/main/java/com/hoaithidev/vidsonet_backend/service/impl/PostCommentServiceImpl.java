package com.hoaithidev.vidsonet_backend.service.impl;

import com.hoaithidev.vidsonet_backend.dto.PostCommentDTO;
import com.hoaithidev.vidsonet_backend.dto.UserDTO;
import com.hoaithidev.vidsonet_backend.enums.NotificationType;
import com.hoaithidev.vidsonet_backend.exception.BadRequestException;
import com.hoaithidev.vidsonet_backend.exception.ResourceNotFoundException;
import com.hoaithidev.vidsonet_backend.model.Post;
import com.hoaithidev.vidsonet_backend.model.PostComment;
import com.hoaithidev.vidsonet_backend.model.User;
import com.hoaithidev.vidsonet_backend.payload.request.PostCommentCreateRequest;
import com.hoaithidev.vidsonet_backend.payload.request.PostCommentUpdateRequest;
import com.hoaithidev.vidsonet_backend.repository.PostCommentRepository;
import com.hoaithidev.vidsonet_backend.repository.PostRepository;
import com.hoaithidev.vidsonet_backend.repository.UserRepository;
import com.hoaithidev.vidsonet_backend.service.NotificationService;
import com.hoaithidev.vidsonet_backend.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PostCommentDTO createComment(Long userId, PostCommentCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + request.getPostId()));

        PostComment comment = PostComment.builder()
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .likeCount(0L)
                .dislikeCount(0L)
                .isHearted(false)
                .user(user)
                .post(post)
                .build();

        // If it's a reply, set parent comment
        if (request.getParentId() != null) {
            PostComment parentComment = postCommentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with id: " + request.getParentId()));

            comment.setParentComment(parentComment);
        }

        PostComment savedComment = postCommentRepository.save(comment);

        // Create notification for post owner if commenter is not the post owner
        if (!userId.equals(post.getUser().getId())) {
            // This would call a method like notificationService.createPostCommentNotification
            // that doesn't exist yet in your NotificationService interface
        }

        return mapToCommentDTO(savedComment);
    }

    @Override
    @Transactional
    public PostCommentDTO updateComment(Long id, Long userId, PostCommentUpdateRequest request) {
        PostComment comment = postCommentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        // Check permissions
        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only update your own comments");
        }

        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        PostComment updatedComment = postCommentRepository.save(comment);

        return mapToCommentDTO(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id, Long userId) {
        PostComment comment = postCommentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        // Check permissions (comment owner or post owner can delete)
        if (!comment.getUser().getId().equals(userId) &&
                !comment.getPost().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only delete your own comments or comments on your posts");
        }

        postCommentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public PostCommentDTO getCommentById(Long id) {
        PostComment comment = postCommentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        return mapToCommentDTO(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostCommentDTO> getCommentsByPostId(Long postId) {
        List<PostComment> comments = postCommentRepository.findByPostIdAndParentCommentIsNullOrderByCreatedAtDesc(postId);

        return comments.stream()
                .map(this::mapToCommentDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostCommentDTO> getRepliesByCommentId(Long commentId) {
        List<PostComment> replies = postCommentRepository.findByParentCommentIdOrderByCreatedAtAsc(commentId);

        return replies.stream()
                .map(this::mapToCommentDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostCommentDTO heartComment(Long id, Long userId) {
        PostComment comment = postCommentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        // Check if user is the post owner
        if (!comment.getPost().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Only post owner can heart comments");
        }

        // Check if already hearted
        if (comment.isHearted()) {
            throw new BadRequestException("Comment is already hearted");
        }

        comment.setHearted(true);
        comment.setHeartedAt(LocalDateTime.now());

        PostComment heartedComment = postCommentRepository.save(comment);

        // Create notification for comment owner if it's not the post owner
        if (!comment.getUser().getId().equals(userId)) {
            // This would call a method like notificationService.createCommentHeartNotification
            // that might not exist yet in your NotificationService interface
        }

        return mapToCommentDTO(heartedComment);
    }

    @Override
    @Transactional
    public PostCommentDTO unhearComment(Long id, Long userId) {
        PostComment comment = postCommentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        // Check if user is the post owner
        if (!comment.getPost().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Only post owner can unheart comments");
        }

        // Check if not hearted
        if (!comment.isHearted()) {
            throw new BadRequestException("Comment is not hearted");
        }

        comment.setHearted(false);
        comment.setHeartedAt(null);

        PostComment unheartedComment = postCommentRepository.save(comment);

        return mapToCommentDTO(unheartedComment);
    }

    private PostCommentDTO mapToCommentDTO(PostComment comment) {
        UserDTO userDTO = UserDTO.builder()
                .id(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .profilePicture(comment.getUser().getProfilePicture())
                .channelName(comment.getUser().getChannelName())
                .channelPicture(comment.getUser().getChannelPicture())
                .build();

        List<PostCommentDTO> replies = new ArrayList<>();

        // Only load replies for top-level comments to avoid recursive loading
        if (comment.getParentComment() == null && comment.getReplies() != null) {
            replies = comment.getReplies().stream()
                    .map(reply -> {
                        UserDTO replyUserDTO = UserDTO.builder()
                                .id(reply.getUser().getId())
                                .username(reply.getUser().getUsername())
                                .profilePicture(reply.getUser().getProfilePicture())
                                .channelName(reply.getUser().getChannelName())
                                .channelPicture(reply.getUser().getChannelPicture())
                                .build();

                        return PostCommentDTO.builder()
                                .id(reply.getId())
                                .content(reply.getContent())
                                .createdAt(reply.getCreatedAt())
                                .updatedAt(reply.getUpdatedAt())
                                .likeCount(reply.getLikeCount())
                                .dislikeCount(reply.getDislikeCount())
                                .isHearted(reply.isHearted())
                                .heartedAt(reply.getHeartedAt())
                                .user(replyUserDTO)
                                .postId(reply.getPost().getId())
                                .parentId(comment.getId())
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        return PostCommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .likeCount(comment.getLikeCount())
                .dislikeCount(comment.getDislikeCount())
                .isHearted(comment.isHearted())
                .heartedAt(comment.getHeartedAt())
                .user(userDTO)
                .postId(comment.getPost().getId())
                .parentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .replies(replies)
                .build();
    }
}