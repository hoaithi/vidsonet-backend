package com.hoaithidev.vidsonet_backend.repository;

import com.hoaithidev.vidsonet_backend.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    /**
     * Find all memberships for a user
     * @param userId The user ID
     * @return List of memberships
     */
    List<Membership> findByUserId(Long userId);

    /**
     * Find all memberships for a channel
     * @param channelId The channel's user ID
     * @return List of memberships
     */
    @Query("SELECT m FROM Membership m WHERE m.membershipTier.user.id = :channelId")
    List<Membership> findByChannelId(Long channelId);

    /**
     * Find active membership for a user and channel
     * @param userId The user ID
     * @param channelId The channel's user ID
     * @return Optional of Membership
     */
    @Query("SELECT m FROM Membership m WHERE m.user.id = :userId AND m.membershipTier.user.id = :channelId AND m.isActive = true")
    Optional<Membership> findActiveByUserIdAndChannelId(Long userId, Long channelId);

    /**
     * Check if user has active membership for a channel
     * @param userId The user ID
     * @param channelId The channel's user ID
     * @param isActive Whether membership is active
     * @return True if active membership exists
     */
    @Query("SELECT COUNT(m) > 0 FROM Membership m WHERE m.user.id = :userId AND m.membershipTier.user.id = :channelId AND m.isActive = :isActive")
    boolean existsByUserIdAndChannelIdAndIsActive(Long userId, Long channelId, boolean isActive);

    /**
     * Count active memberships for a membership tier
     * @param membershipTierId The membership tier ID
     * @param isActive Whether membership is active
     * @return Count of active memberships
     */
    long countByMembershipTierIdAndIsActive(Long membershipTierId, boolean isActive);
}