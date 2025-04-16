package com.hoaithidev.vidsonet_backend.repository;

import com.hoaithidev.vidsonet_backend.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    @Query("SELECT COUNT(m) > 0 FROM Membership m WHERE m.user.id = :userId AND m.membershipTier.user.id = :channelId AND m.isActive = :isActive")
    boolean existsByUserIdAndChannelIdAndIsActive(Long userId, Long channelId, boolean isActive);
}

