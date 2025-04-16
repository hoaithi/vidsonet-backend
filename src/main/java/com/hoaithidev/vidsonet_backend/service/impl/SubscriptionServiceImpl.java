package com.hoaithidev.vidsonet_backend.service.impl;

import com.hoaithidev.vidsonet_backend.dto.SubscriptionDTO;
import com.hoaithidev.vidsonet_backend.dto.UserDTO;
//import com.hoaithidev.vidsonet_backend.exception.BadRequestException;
import com.hoaithidev.vidsonet_backend.exception.DuplicateResourceException;
import com.hoaithidev.vidsonet_backend.exception.ErrorCode;
import com.hoaithidev.vidsonet_backend.exception.ResourceNotFoundException;
import com.hoaithidev.vidsonet_backend.exception.VidsonetException;
import com.hoaithidev.vidsonet_backend.model.Subscription;
import com.hoaithidev.vidsonet_backend.model.User;
import com.hoaithidev.vidsonet_backend.repository.NotificationRepository;
import com.hoaithidev.vidsonet_backend.repository.SubscriptionRepository;
import com.hoaithidev.vidsonet_backend.repository.UserRepository;
import com.hoaithidev.vidsonet_backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public SubscriptionDTO subscribe(Long userId, Long channelId, boolean notificationEnabled) {
        // Kiểm tra user và channel tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User channel = userRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));

        // Kiểm tra channel có hợp lệ (có tên kênh)
        if (channel.getChannelName() == null || channel.getChannelName().isEmpty()) {
            throw new VidsonetException(ErrorCode.USER_NOT_FOUND, "Channel is not valid");
        }

        // Không thể tự đăng ký kênh của mình
        if (userId.equals(channelId)) {
            throw new VidsonetException(ErrorCode.USER_NOT_FOUND, "Cannot subscribe to your own channel");
        }

        // Kiểm tra đã đăng ký chưa
        if (subscriptionRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw new DuplicateResourceException("Already subscribed to this channel");
        }

        // Tạo subscription mới
        Subscription subscription = Subscription.builder()
                .user(user)
                .channel(channel)
                .subscribedAt(LocalDateTime.now())
                .notificationEnabled(notificationEnabled)
                .build();

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // Tạo thông báo cho chủ kênh
        createSubscriptionNotification(user, channel);

        return mapToSubscriptionDTO(savedSubscription);
    }

    @Override
    @Transactional
    public void unsubscribe(Long userId, Long channelId) {
        Subscription subscription = subscriptionRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        subscriptionRepository.delete(subscription);
    }

    @Override
    @Transactional
    public void toggleNotification(Long userId, Long channelId, boolean enabled) {
        Subscription subscription = subscriptionRepository.findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        subscription.setNotificationEnabled(enabled);
        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId).stream()
                .map(this::mapToSubscriptionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getChannelSubscribers(Long channelId) {
        return subscriptionRepository.findByChannelId(channelId).stream()
                .map(this::mapToSubscriptionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getSubscriberCount(Long channelId) {
        return subscriptionRepository.countByChannelId(channelId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSubscribed(Long userId, Long channelId) {
        return subscriptionRepository.existsByUserIdAndChannelId(userId, channelId);
    }

    private void createSubscriptionNotification(User subscriber, User channel) {
        // TODO: Implement notification creation
    }

    private SubscriptionDTO mapToSubscriptionDTO(Subscription subscription) {
        UserDTO channelDTO = UserDTO.builder()
                .id(subscription.getChannel().getId())
                .username(subscription.getChannel().getUsername())
                .channelName(subscription.getChannel().getChannelName())
                .channelPicture(subscription.getChannel().getChannelPicture())
                .build();

        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .channel(channelDTO)
                .subscribedAt(subscription.getSubscribedAt())
                .notificationEnabled(subscription.isNotificationEnabled())
                .build();
    }
}
