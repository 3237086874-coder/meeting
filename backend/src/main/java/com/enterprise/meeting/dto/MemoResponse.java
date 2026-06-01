package com.enterprise.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemoResponse {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String priority;
    private String reminderAt;
    private Boolean isCompleted;
    private String color;
    private String createdAt;
    private String updatedAt;
}
