package com.enterprise.meeting.dto;

import lombok.Data;

@Data
public class CreateMeetingRequest {
    private String title;
    private String description;
    private String meetingTime;
    private String location;
    private Integer durationMinutes;
    private Long createdBy;
    private Long assignedReviewer;
}
