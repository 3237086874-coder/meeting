package com.enterprise.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private Long meetingId;
    private String title;
    private String description;
    private Long assignedTo;
    private Long assignedBy;
    private String priority;
    private String dueDate;
    private String state;
    private String completionNote;
    private Boolean isAiExtracted;
    private Boolean isVisible;
    private String createdAt;
    private String updatedAt;
    private String completedAt;
}
