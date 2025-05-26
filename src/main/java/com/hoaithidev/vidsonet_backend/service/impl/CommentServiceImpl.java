package com.hoaithidev.vidsonet_backend.service.impl;

import com.hoaithidev.vidsonet_backend.dto.comment.CommentDTO;
import com.hoaithidev.vidsonet_backend.dto.user.UserDTO;
import com.hoaithidev.vidsonet_backend.exception.ErrorCode;
import com.hoaithidev.vidsonet_backend.exception.ResourceNotFoundException;
import com.hoaithidev.vidsonet_backend.exception.VidsonetException;
import com.hoaithidev.vidsonet_backend.model.Comment;
import com.hoaithidev.vidsonet_backend.model.User;
import com.hoaithidev.vidsonet_backend.model.Video;
import com.hoaithidev.vidsonet_backend.dto.comment.CommentCreateRequest;
import com.hoaithidev.vidsonet_backend.dto.comment.CommentUpdateRequest;
import com.hoaithidev.vidsonet_backend.repository.CommentRepository;
import com.hoaithidev.vidsonet_backend.repository.UserRepository;
import com.hoaithidev.vidsonet_backend.repository.VideoRepository;
import com.hoaithidev.vidsonet_backend.service.CommentService;
import com.hoaithidev.vidsonet_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public CommentDTO createComment(Long userId, CommentCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Video video = null;
        if(request.getVideoId() == null && request.getParentId() !=null) {
            Comment parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with id: " + request.getParentId()));
            video = videoRepository.findById(parentComment.getVideo().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + parentComment.getVideo().getId()));
        }else{
            assert request.getVideoId() != null;
            video = videoRepository.findById(request.getVideoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + request.getVideoId()));
        }
        Comment comment = Comment.builder()
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .likeCount(0L)
                .dislikeCount(0L)
                .isPinned(false)
                .isHearted(false)
                .user(user)
                .video(video)
                .build();

        // If parentId is provided, set as reply
        if (request.getParentId() != null) {
            Comment parentComment = (Comment) commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found with id: " + request.getParentId()));

            comment.setParentComment(parentComment);
        }

        Comment savedComment = commentRepository.save(comment);
        notificationService.createCommentNotification(savedComment);

        return mapToCommentDTO(savedComment);
    }

    @Override
    @Transactional
    public CommentDTO updateComment(Long commentId, Long userId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        // Check if user is the comment owner
        if (!comment.getUser().getId().equals(userId)) {
            throw new VidsonetException(ErrorCode.FORBIDDEN, "You can only update your own comments");
        }

        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);

        return mapToCommentDTO(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        // Check if user is the comment owner or video owner
        if (!comment.getUser().getId().equals(userId) &&
                !comment.getVideo().getUser().getId().equals(userId)) {
            throw new VidsonetException(ErrorCode.FORBIDDEN, "You can only delete your own comments or comments on your videos");
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDTO getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        return mapToCommentDTO(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByVideoId(Long videoId) {
        // Get only top-level comments (no parent)
        List<Comment> comments = commentRepository.findByVideoIdAndParentCommentIsNullOrderByCreatedAtDesc(videoId);

        return comments.stream()
                .map(this::mapToCommentDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getRepliesByCommentId(Long commentId) {
        log.error("Comment ID: {}", commentId);
        List<Comment> replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(commentId);
        log.error("Replies: {}", replies);

        return replies.stream()
                .map(this::mapToCommentDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDTO heartComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        // Check if user is the video owner
        if (!comment.getVideo().getUser().getId().equals(userId)) {
            throw new VidsonetException(ErrorCode.NOT_VIDEO_OWNER);
        }

        // Check if comment is already hearted
        if (comment.isHearted()) {
            throw new VidsonetException(ErrorCode.ALREADY_HEARTED);
        }

        comment.setHearted(true);
        comment.setHeartedAt(LocalDateTime.now());

        Comment heartedComment = commentRepository.save(comment);

        return mapToCommentDTO(heartedComment);
    }

    @Override
    @Transactional
    public CommentDTO unhearComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        // Check if user is the video owner
        if (!comment.getVideo().getUser().getId().equals(userId)) {
            throw new VidsonetException(ErrorCode.NOT_VIDEO_OWNER);
        }

        // Check if comment is not hearted
        if (!comment.isHearted()) {
            throw new VidsonetException(ErrorCode.INVALID_REQUEST, "Comment is not hearted");
        }

        comment.setHearted(false);
        comment.setHeartedAt(null);

        Comment unhearted = commentRepository.save(comment);

        return mapToCommentDTO(unhearted);
    }

    @Override
    @Transactional
    public CommentDTO pinComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        // Check if user is the video owner
        if (!comment.getVideo().getUser().getId().equals(userId)) {
            throw new VidsonetException(ErrorCode.FORBIDDEN, "Only video owner can pin comments");
        }

        // Unpin any existing pinned comments for this video
        List<Comment> pinnedComments = commentRepository.findByVideoIdAndIsPinnedTrue(comment.getVideo().getId());
        pinnedComments.forEach(c -> {
            c.setPinned(false);
            commentRepository.save(c);
        });

        // Pin the current comment
        comment.setPinned(true);
        Comment pinnedComment = commentRepository.save(comment);

        return mapToCommentDTO(pinnedComment);
    }

    @Override
    @Transactional
    public CommentDTO unpinComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        // Check if user is the video owner
        if (!comment.getVideo().getUser().getId().equals(userId)) {
            throw new VidsonetException(ErrorCode.FORBIDDEN, "Only video owner can unpin comments");
        }

        // Check if comment is pinned
        if (!comment.isPinned()) {
            throw new VidsonetException(ErrorCode.INVALID_REQUEST, "Comment is not pinned");
        }

        comment.setPinned(false);
        Comment unpinnedComment = commentRepository.save(comment);

        return mapToCommentDTO(unpinnedComment);
    }

    private CommentDTO mapToCommentDTO(Comment comment) {
        UserDTO userDTO = UserDTO.builder()
                .id(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .profilePicture(comment.getUser().getProfilePicture())
                .channelName(comment.getUser().getChannelName())
                .channelPicture(comment.getUser().getChannelPicture())
                .build();

        List<CommentDTO> replies = new ArrayList<>();

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

                        return CommentDTO.builder()
                                .id(reply.getId())
                                .content(reply.getContent())
                                .createdAt(reply.getCreatedAt())
                                .updatedAt(reply.getUpdatedAt())
                                .likeCount(reply.getLikeCount())
                                .dislikeCount(reply.getDislikeCount())
                                .isPinned(reply.isPinned())
                                .isHearted(reply.isHearted())
                                .heartedAt(reply.getHeartedAt())
                                .user(replyUserDTO)
                                .videoId(reply.getVideo().getId())
                                .parentId(comment.getId())
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .likeCount(comment.getLikeCount())
                .dislikeCount(comment.getDislikeCount())
                .isPinned(comment.isPinned())
                .isHearted(comment.isHearted())
                .heartedAt(comment.getHeartedAt())
                .user(userDTO)
                .videoId(comment.getVideo().getId())
                .parentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .replies(replies)
                .build();
    }
}
