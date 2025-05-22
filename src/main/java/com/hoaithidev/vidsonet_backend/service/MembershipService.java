package com.hoaithidev.vidsonet_backend.service;

import com.hoaithidev.vidsonet_backend.dto.membership.MembershipDTO;
import com.hoaithidev.vidsonet_backend.dto.membership.MembershipTierCreateRequest;
import com.hoaithidev.vidsonet_backend.dto.membership.MembershipTierDTO;
import com.hoaithidev.vidsonet_backend.dto.membership.MembershipTierUpdateRequest;
import com.hoaithidev.vidsonet_backend.model.Membership;

import java.util.List;

public interface MembershipService {

    /**
     * Creates a new membership tier for a channel
     * @param userId The channel owner's user ID
     * @param request The membership tier creation request
     * @return The created membership tier
     */
    MembershipTierDTO createMembershipTier(Long userId, MembershipTierCreateRequest request);

    /**
     * Updates an existing membership tier
     * @param tierId The tier ID to update
     * @param userId The channel owner's user ID
     * @param request The update request
     * @return The updated membership tier
     */
    MembershipTierDTO updateMembershipTier(Long tierId, Long userId, MembershipTierUpdateRequest request);

    /**
     * Deletes a membership tier
     * @param tierId The tier ID to delete
     * @param userId The channel owner's user ID
     */
    void deleteMembershipTier(Long tierId, Long userId);

    /**
     * Gets all membership tiers for a channel
     * @param channelId The channel user ID
     * @return List of membership tiers
     */
    List<MembershipTierDTO> getChannelMembershipTiers(Long channelId);

    /**
     * Gets a specific membership tier by ID
     * @param tierId The tier ID
     * @return The membership tier
     */
    MembershipTierDTO getMembershipTierById(Long tierId);

    /**
     * Gets all of a user's memberships (channels they are subscribed to)
     * @param userId The user ID
     * @return List of memberships
     */
    List<MembershipDTO> getUserMemberships(Long userId);

    /**
     * Gets all subscribers of a channel
     * @param channelId The channel user ID
     * @return List of memberships
     */
    List<MembershipDTO> getChannelMembers(Long channelId);

    /**
     * Checks if a user has an active membership for a channel
     * @param userId The user ID
     * @param channelId The channel user ID
     * @return True if the user has an active membership
     */
    boolean hasActiveChannelMembership(Long userId, Long channelId);
}