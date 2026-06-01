package com.enterprise.meeting.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String displayName;
    private String phone;
    private String email;
    private String title;
    private Boolean isActive;
    private Long roleId;
    private Long departmentId;
}
