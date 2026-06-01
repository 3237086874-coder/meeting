package com.enterprise.meeting.dto;

import lombok.Data;

@Data
public class MemoRequest {
    private String title;
    private String content;
    private String priority;
    private String reminderAt;
    private String color;
    private Boolean isCompleted;
}
