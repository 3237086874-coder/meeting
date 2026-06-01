package com.enterprise.meeting.controller;

import com.enterprise.meeting.dto.ApiResponse;
import com.enterprise.meeting.dto.NotificationResponse;
import com.enterprise.meeting.dto.PaginationDto;
import com.enterprise.meeting.entity.Notification;
import com.enterprise.meeting.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Notification> notifPage = notificationService.getNotificationsPage(userId, page, size);
        List<NotificationResponse> items = notificationService.toResponseList(notifPage.getContent());
        PaginationDto pagination = new PaginationDto(page, size, notifPage.getTotalElements(), notifPage.getTotalPages());
        return ApiResponse.success(items, pagination);
    }

    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Integer>> getUnreadCount(@RequestParam Long userId) {
        return ApiResponse.success(notificationService.getUnreadCount(userId));
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ApiResponse.<Void>success(null);
    }

    @PostMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(@RequestParam Long userId) {
        notificationService.markAllAsRead(userId);
        return ApiResponse.<Void>success(null);
    }
}
