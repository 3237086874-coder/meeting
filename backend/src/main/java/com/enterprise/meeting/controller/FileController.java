package com.enterprise.meeting.controller;

import com.enterprise.meeting.dto.ApiResponse;
import com.enterprise.meeting.dto.FileDto;
import com.enterprise.meeting.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ApiResponse<FileDto> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long meetingId,
            @RequestParam(required = false) Long taskId,
            @RequestParam(defaultValue = "ATTACHMENT") String fileType,
            @RequestParam Long uploadedBy) {
        return ApiResponse.success(
                fileService.uploadFile(file, meetingId, taskId, fileType, uploadedBy));
    }

    @GetMapping("/meeting/{meetingId}")
    public ApiResponse<List<FileDto>> getMeetingFiles(@PathVariable Long meetingId) {
        return ApiResponse.success(fileService.getFilesByMeeting(meetingId));
    }

    @GetMapping("/task/{taskId}")
    public ApiResponse<List<FileDto>> getTaskFiles(@PathVariable Long taskId) {
        return ApiResponse.success(fileService.getFilesByTask(taskId));
    }
}
