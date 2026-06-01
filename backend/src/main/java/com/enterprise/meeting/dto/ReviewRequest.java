package com.enterprise.meeting.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Boolean approved;
    private String comment;
}
