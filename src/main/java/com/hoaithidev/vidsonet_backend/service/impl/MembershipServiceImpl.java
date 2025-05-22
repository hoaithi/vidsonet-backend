package com.hoaithidev.vidsonet_backend.service.impl;

import com.hoaithidev.vidsonet_backend.dto.UserDTO;
import com.hoaithidev.vidsonet_backend.dto.membership.MembershipDTO;
import com.hoaithidev.vidsonet_backend.dto.membership.MembershipTierCreateRequest;
import com.hoaithidev.vidsonet_backend.dto.membership.MembershipTierDTO;
import com.hoaithidev.vidsonet_backend.dto.membership.MembershipTierUpdateRequest;
import com.hoaithidev.vidsonet_backend.exception.ErrorCode;
import com.hoaithidev.vidsonet_backend.exception.ResourceNotFoundException;
import com.hoaithidev.vidsonet_backend.exception.VidsonetException;
import com.hoaithidev.vidsonet_backend.model.Membership;
import com.hoaithidev.vidsonet_backend.model.MembershipTier;
import com.hoaithidev.vidsonet_backend.model.User;
import com.hoaithidev.vidsonet_backend.repository.MembershipRepository;
import com.hoaithidev.vidsonet_backend.repository.MembershipTierRepository;
import com.hoaithidev.vidsonet_backend.repository.UserRepository;
import com.hoaithidev.vidsonet_backend.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final MembershipTierRepository membershipTierRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MembershipTierDTO createMembershipTier(Long userId, MembershipTierCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Kiểm tra xem user có kênh chưa
        if (user.getChannelName() == null || user.getChannelName().isEmpty()) {
            throw new VidsonetException(ErrorCode.INVALID_REQUEST, "You need to set up your channel first");
        }

        // Tạo membership tier mới
        MembershipTier membershipTier = MembershipTier.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .durationMonths(request.getDurationMonths())
                .isActive(true)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        MembershipTier savedTier = membershipTierRepository.save(membershipTier);

        return mapToMembershipTierDTO(savedTier);
    }

    @Override
    @Transactional
    public MembershipTierDTO updateMembershipTier(Long tierId, Long userId, MembershipTierUpdateRequest request) {
        MembershipTier tier = membershipTierRepository.findById(tierId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership tier not found with id: " + tierId));

        // Kiểm tra quyền sở hữu
        if (!tier.getUser().getId().equals(userId)) {
            throw new VidsonetException(ErrorCode.FORBIDDEN, "You can only update your own membership tiers");
        }

        // Cập nhật thông tin
        if (request.getName() != null) {
            tier.setName(request.getName());
        }

        if (request.getDescription() != null) {
            tier.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            tier.setPrice(request.getPrice());
        }

        if (request.getDurationMonths() != null) {
            tier.setDurationMonths(request.getDurationMonths());
        }

        if (request.getIsActive() != null) {
            tier.setActive(request.getIsActive());
        }

        tier.setUpdatedAt(LocalDateTime.now());

        MembershipTier updatedTier = membershipTierRepository.save(tier);

        return mapToMembershipTierDTO(updatedTier);
    }

    @Override
    @Transactional
    public void deleteMembershipTier(Long tierId, Long userId) {
        MembershipTier tier = membershipTierRepository.findById(tierId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership tier not found with id: " + tierId));

        // Kiểm tra quyền sở hữu
        if (!tier.getUser().getId().equals(userId)) {
            throw new VidsonetException(ErrorCode.FORBIDDEN, "You can only delete your own membership tiers");
        }

        // Kiểm tra xem có subscription active nào đang sử dụng tier này không
        long activeSubscriptions = membershipRepository.countByMembershipTierIdAndIsActive(tierId, true);
        if (activeSubscriptions > 0) {
            // Thay vì xóa, ta đánh dấu là không active
            tier.setActive(false);
            tier.setUpdatedAt(LocalDateTime.now());
            membershipTierRepository.save(tier);
        } else {
            membershipTierRepository.delete(tier);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipTierDTO> getChannelMembershipTiers(Long channelId) {
        User channel = userRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));

        return membershipTierRepository.findByUserIdOrderByPriceAsc(channelId).stream()
                .map(this::mapToMembershipTierDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipTierDTO getMembershipTierById(Long tierId) {
        MembershipTier tier = membershipTierRepository.findById(tierId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership tier not found with id: " + tierId));

        return mapToMembershipTierDTO(tier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipDTO> getUserMemberships(Long userId) {
        return membershipRepository.findByUserId(userId).stream()
                .map(this::mapToMembershipDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipDTO> getChannelMembers(Long channelId) {
        return membershipRepository.findByChannelId(channelId).stream()
                .map(this::mapToMembershipDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveChannelMembership(Long userId, Long channelId) {
        return membershipRepository.existsByUserIdAndChannelIdAndIsActive(userId, channelId, true);
    }

    // Helper methods to map entities to DTOs
    private MembershipTierDTO mapToMembershipTierDTO(MembershipTier tier) {
        return MembershipTierDTO.builder()
                .id(tier.getId())
                .name(tier.getName())
                .description(tier.getDescription())
                .price(tier.getPrice())
                .durationMonths(tier.getDurationMonths())
                .isActive(tier.isActive())
                .createdAt(tier.getCreatedAt())
                .updatedAt(tier.getUpdatedAt())
                .channelId(tier.getUser().getId())
                .channelName(tier.getUser().getChannelName())
                .build();
    }

    private MembershipDTO mapToMembershipDTO(Membership membership) {
        MembershipTier tier = membership.getMembershipTier();
        User subscriber = membership.getUser();
        User channel = tier.getUser();

        return MembershipDTO.builder()
                .id(membership.getId())
                .startDate(membership.getStartDate())
                .endDate(membership.getEndDate())
                .isActive(membership.isActive())
                .createdAt(membership.getCreatedAt())
                .updatedAt(membership.getUpdatedAt())
                .tier(mapToMembershipTierDTO(tier))
                .subscriber(UserDTO.builder()
                        .id(subscriber.getId())
                        .username(subscriber.getUsername())
                        .profilePicture(subscriber.getProfilePicture())
                        .build())
                .channel(UserDTO.builder()
                        .id(channel.getId())
                        .username(channel.getUsername())
                        .channelName(channel.getChannelName())
                        .channelPicture(channel.getChannelPicture())
                        .build())
                .build();
    }
}