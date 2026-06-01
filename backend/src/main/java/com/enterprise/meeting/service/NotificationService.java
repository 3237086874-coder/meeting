package com.enterprise.meeting.service;

import com.enterprise.meeting.dto.NotificationResponse;
import com.enterprise.meeting.entity.Notification;
import com.enterprise.meeting.exception.BusinessException;
import com.enterprise.meeting.repository.NotificationRepository;
import com.enterprise.meeting.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SecurityUtil securityUtil;

    public Page<Notification> getNotificationsPage(Long userId, int page, int size) {
        securityUtil.getCurrentUser();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
    }

    public Map<String, Integer> getUnreadCount(Long userId) {
        securityUtil.getCurrentUser();
        int count = (int) notificationRepository.countByUserIdAndIsReadFalse(userId);
        return Map.of("count", count);
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "通知不存在"));
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> {
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(unread);
    }

    public List<NotificationResponse> toResponseList(List<Notification> notifications) {
        return notifications.stream().map(this::toResponse).toList();
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUser().getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.getReferenceType(),
                notification.getReferenceId(),
                notification.getIsRead(),
                notification.getCreatedAt() != null ? notification.getCreatedAt().toString() : null
        );
    }
}
