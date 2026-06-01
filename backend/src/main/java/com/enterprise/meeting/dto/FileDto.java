package com.enterprise.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDto {
    private Long id;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String ossKey;
    private String fileType;
    private Long uploadedBy;
    private String createdAt;
}
