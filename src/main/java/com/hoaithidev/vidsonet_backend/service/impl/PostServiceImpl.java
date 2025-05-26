package com.hoaithidev.vidsonet_backend.service.impl;

import com.hoaithidev.vidsonet_backend.dto.post.PostDTO;
import com.hoaithidev.vidsonet_backend.dto.user.UserDTO;
import com.hoaithidev.vidsonet_backend.enums.ReactionType;
import com.hoaithidev.vidsonet_backend.exception.ResourceNotFoundException;
import com.hoaithidev.vidsonet_backend.model.Post;
import com.hoaithidev.vidsonet_backend.model.PostReaction;
import com.hoaithidev.vidsonet_backend.model.User;
import com.hoaithidev.vidsonet_backend.dto.post.PostCreateRequest;
import com.hoaithidev.vidsonet_backend.dto.post.PostUpdateRequest;
import com.hoaithidev.vidsonet_backend.repository.PostCommentRepository;
import com.hoaithidev.vidsonet_backend.repository.PostReactionRepository;
import com.hoaithidev.vidsonet_backend.repository.PostRepository;
import com.hoaithidev.vidsonet_backend.repository.UserRepository;
import com.hoaithidev.vidsonet_backend.service.NotificationService;
import com.hoaithidev.vidsonet_backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;
    private final PostReactionRepository postReactionRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PostDTO createPost(Long userId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if user has a channel
        if (user.getChannelName() == null || user.getChannelName().isEmpty()) {
            throw new AccessDeniedException("You need to set up your channel first");
        }

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .likeCount(0L)
                .dislikeCount(0L)
                .user(user)
                .build();

        Post savedPost = postRepository.save(post);

        // Create notifications for subscribers
        notificationService.createPostNotification(savedPost);

        return mapToPostDTO(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        return mapToPostDTO(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsByUserId(Long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return postRepository.findByUserId(userId, pageable)
                .map(this::mapToPostDTO);
    }

    @Override
    @Transactional
    public PostDTO updatePost(Long id, Long userId, PostUpdateRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        // Check permissions
        if (!post.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only update your own posts");
        }

        // Update fields if provided
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }

        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }

        post.setUpdatedAt(LocalDateTime.now());

        Post updatedPost = postRepository.save(post);

        return mapToPostDTO(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        // Check permissions
        if (!post.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only delete your own posts");
        }

        // Delete all related entities
        postCommentRepository.deleteByPostId(id);
        postReactionRepository.deleteByPostId(id);

        // Delete post
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public PostDTO likePost(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if user already reacted
        Optional<PostReaction> existingReaction = postReactionRepository.findByUserIdAndPostId(userId, id);

        if (existingReaction.isPresent()) {
            PostReaction reaction = existingReaction.get();

            // If already liked, do nothing
            if (reaction.getReactionType() == ReactionType.LIKE) {
                return mapToPostDTO(post);
            }

            // If disliked, change to like and update counts
            reaction.setReactionType(ReactionType.LIKE);
            reaction.setCreatedAt(LocalDateTime.now());
            postReactionRepository.save(reaction);

            post.setLikeCount(post.getLikeCount() + 1);
            post.setDislikeCount(post.getDislikeCount() - 1);
        } else {
            // Create new reaction
            PostReaction reaction = PostReaction.builder()
                    .user(user)
                    .post(post)
                    .reactionType(ReactionType.LIKE)
                    .createdAt(LocalDateTime.now())
                    .build();

            postReactionRepository.save(reaction);

            post.setLikeCount(post.getLikeCount() + 1);
        }

        Post updatedPost = postRepository.save(post);

        // Create notification for post owner
        if (!userId.equals(post.getUser().getId())) {
            // Add notification logic here if needed
        }

        return mapToPostDTO(updatedPost);
    }

    @Override
    @Transactional
    public PostDTO dislikePost(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if user already reacted
        Optional<PostReaction> existingReaction = postReactionRepository.findByUserIdAndPostId(userId, id);

        if (existingReaction.isPresent()) {
            PostReaction reaction = existingReaction.get();

            // If already disliked, do nothing
            if (reaction.getReactionType() == ReactionType.DISLIKE) {
                return mapToPostDTO(post);
            }

            // If liked, change to dislike and update counts
            reaction.setReactionType(ReactionType.DISLIKE);
            reaction.setCreatedAt(LocalDateTime.now());
            postReactionRepository.save(reaction);

            post.setLikeCount(post.getLikeCount() - 1);
            post.setDislikeCount(post.getDislikeCount() + 1);
        } else {
            // Create new reaction
            PostReaction reaction = PostReaction.builder()
                    .user(user)
                    .post(post)
                    .reactionType(ReactionType.DISLIKE)
                    .createdAt(LocalDateTime.now())
                    .build();

            postReactionRepository.save(reaction);

            post.setDislikeCount(post.getDislikeCount() + 1);
        }

        Post updatedPost = postRepository.save(post);

        return mapToPostDTO(updatedPost);
    }

    private PostDTO mapToPostDTO(Post post) {
        UserDTO userDTO = UserDTO.builder()
                .id(post.getUser().getId())
                .username(post.getUser().getUsername())
                .profilePicture(post.getUser().getProfilePicture())
                .channelName(post.getUser().getChannelName())
                .channelPicture(post.getUser().getChannelPicture())
                .build();

        // Count comments
        long commentCount = postCommentRepository.countByPostId(post.getId());

        return PostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(post.getLikeCount())
                .dislikeCount(post.getDislikeCount())
                .user(userDTO)
                .commentCount((int) commentCount)
                .build();
    }
}