package com.enterprise.meeting.dto;

import lombok.Data;

@Data
public class CreateMemoRequest {
    private String title;
    private String content;
    private String priority = "NORMAL";
    private String reminderAt;
    private String color;
}
