package com.hoaithidev.vidsonet_backend.repository;

import com.hoaithidev.vidsonet_backend.model.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {

    /**
     * Find all membership tiers for a channel
     * @param userId The channel's user ID
     * @return List of membership tiers ordered by price
     */
    List<MembershipTier> findByUserIdOrderByPriceAsc(Long userId);

    /**
     * Find active membership tiers for a channel
     * @param userId The channel's user ID
     * @param isActive Whether the tier is active
     * @return List of active membership tiers
     */
    List<MembershipTier> findByUserIdAndIsActive(Long userId, boolean isActive);
}