package com.enterprise.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class MeetingResponse {
    private Long id;
    private String title;
    private String description;
    private String meetingTime;
    private String location;
    private Integer durationMinutes;
    private Long createdBy;
    private Long assignedReviewer;
    private String state;
    private Map<String, String> stateMetadata;
    private Integer aiRetryCount;
    private String aiError;
    private String createdAt;
    private String updatedAt;
}
