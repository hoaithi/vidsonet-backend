package com.hoaithidev.vidsonet_backend.service.impl;

import com.hoaithidev.vidsonet_backend.dto.UserDTO;
import com.hoaithidev.vidsonet_backend.exception.DuplicateResourceException;
import com.hoaithidev.vidsonet_backend.exception.ResourceNotFoundException;
import com.hoaithidev.vidsonet_backend.model.User;
import com.hoaithidev.vidsonet_backend.payload.request.UpdateProfileRequest;
import com.hoaithidev.vidsonet_backend.repository.SubscriptionRepository;
import com.hoaithidev.vidsonet_backend.repository.UserRepository;
import com.hoaithidev.vidsonet_backend.service.FileStorageService;
import com.hoaithidev.vidsonet_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Get subscriber count
        long subscriberCount = subscriptionRepository.countByChannelId(id);

        return mapToUserDTO(user, subscriberCount);
    }



    @Transactional(readOnly = true)
    @Override
    public UserDTO getChannelByUserId(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (user.getChannelName() == null || user.getChannelName().isEmpty()) {
            throw new ResourceNotFoundException("Channel not found for user id: " + id);
        }

        // Get subscriber count
        long subscriberCount = subscriptionRepository.countByChannelId(id);

        return mapToUserDTO(user, subscriberCount);
    }

    @Transactional
    @Override
    public UserDTO updateProfile(Long id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update username if provided and different
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }

        // Update email if provided and different
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        // Update channel name if provided and different
        if (request.getChannelName() != null && !request.getChannelName().equals(user.getChannelName())) {
            if (userRepository.existsByChannelName(request.getChannelName())) {
                throw new DuplicateResourceException("Channel name already exists");
            }
            user.setChannelName(request.getChannelName());
        }

        // Update other fields if provided
        if (request.getProfilePicture() != null && !request.getProfilePicture().isEmpty()) {
            String profilePath = fileStorageService.storeFile(request.getProfilePicture(), "profile_pictures");
            String videoUrl = fileStorageService.getFileUrl(profilePath);
            user.setProfilePicture(videoUrl);
        }

        if (request.getChannelDescription() != null) {
            user.setChannelDescription(request.getChannelDescription());
        }

        if (request.getChannelPicture() != null && !request.getChannelPicture().isEmpty()) {
            String channelPath = fileStorageService.storeFile(request.getChannelPicture(), "channel_pictures");
            String channelUrl = fileStorageService.getFileUrl(channelPath);
            user.setChannelPicture(channelUrl);
        }

        if (request.getBannerImage() != null && !request.getBannerImage().isEmpty()) {
            String bannerPath = fileStorageService.storeFile(request.getBannerImage(), "banner_images");
            String bannerUrl = fileStorageService.getFileUrl(bannerPath);
            user.setBannerImage(bannerUrl);
        }

        User updatedUser = userRepository.save(user);

        // Get subscriber count
        long subscriberCount = subscriptionRepository.countByChannelId(id);

        return mapToUserDTO(updatedUser, subscriberCount);
    }

    @Override
    public List<UserDTO> getAllUser() {
        return userRepository.findAll().stream()
                .map(user -> {
                    long subscriberCount = subscriptionRepository.countByChannelId(user.getId());
                    return mapToUserDTO(user, subscriberCount);
                })
                .toList();
    }

    private UserDTO mapToUserDTO(User user, long subscriberCount) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .createdAt(user.getCreatedAt())
                .channelName(user.getChannelName())
                .channelDescription(user.getChannelDescription())
                .channelPicture(user.getChannelPicture())
                .bannerImage(user.getBannerImage())
                .subscriberCount(subscriberCount)
                .build();
    }
}