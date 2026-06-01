package com.enterprise.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String body;
    private String referenceType;
    private Long referenceId;
    private Boolean isRead;
    private String createdAt;
}
