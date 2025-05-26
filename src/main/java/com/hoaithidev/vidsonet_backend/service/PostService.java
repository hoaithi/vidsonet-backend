package com.hoaithidev.vidsonet_backend.service;


import com.hoaithidev.vidsonet_backend.dto.post.PostDTO;
import com.hoaithidev.vidsonet_backend.dto.post.PostCreateRequest;
import com.hoaithidev.vidsonet_backend.dto.post.PostUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    PostDTO createPost(Long userId, PostCreateRequest request);
    PostDTO getPostById(Long id);
    Page<PostDTO> getPostsByUserId(Long userId, Pageable pageable);
    PostDTO updatePost(Long id, Long userId, PostUpdateRequest request);
    void deletePost(Long id, Long userId);
    PostDTO likePost(Long id, Long userId);
    PostDTO dislikePost(Long id, Long userId);
}