package com.hoaithidev.vidsonet_backend.repository;

import com.hoaithidev.vidsonet_backend.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    long countByChannelId(Long id);

    boolean existsByUserIdAndChannelId(Long userId, Long channelId);

    Optional<Subscription> findByUserIdAndChannelId(Long userId, Long channelId);

    List<Subscription> findByUserId(Long userId);

    List<Subscription> findByChannelId(Long channelId);

    List<Subscription> findByChannelIdAndNotificationEnabled(Long id, boolean b);
}
