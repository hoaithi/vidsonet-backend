package com.hoaithidev.vidsonet_backend.service;

import com.hoaithidev.vidsonet_backend.dto.SubscriptionDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SubscriptionService {
    SubscriptionDTO subscribe(Long userId, Long channelId, boolean notificationEnabled);
    void unsubscribe(Long userId, Long channelId);
    void toggleNotification(Long userId, Long channelId, boolean enabled);
    List<SubscriptionDTO> getUserSubscriptions(Long userId);
    List<SubscriptionDTO> getChannelSubscribers(Long channelId);
    long getSubscriberCount(Long channelId);
    boolean isSubscribed(Long userId, Long channelId);
}