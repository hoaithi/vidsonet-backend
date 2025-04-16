package com.hoaithidev.vidsonet_backend.security;

import com.hoaithidev.vidsonet_backend.model.Video;
import com.hoaithidev.vidsonet_backend.repository.UserRepository;
import com.hoaithidev.vidsonet_backend.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VideoSecurity {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    public boolean isVideoOwner(Long videoId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }

        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if (videoOpt.isEmpty()) {
            return false;
        }

        Video video = videoOpt.get();
        return video.getUser().getId().equals(currentUserId);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            Long id = ((UserPrincipal) principal).getId();
            // This would require a utility to extract user ID from username
            // For simplicity, assuming username is the user ID

            try {
                return id;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}