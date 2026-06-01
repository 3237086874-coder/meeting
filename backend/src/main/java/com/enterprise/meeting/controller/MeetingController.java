package com.enterprise.meeting.controller;

import com.enterprise.meeting.dto.*;
import com.enterprise.meeting.entity.Meeting;
import com.enterprise.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    public ApiResponse<List<MeetingResponse>> getMeetings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId) {
        Page<Meeting> meetingPage = meetingService.getMeetingsPage(userId, page, size);
        List<MeetingResponse> items = meetingService.toResponseList(meetingPage.getContent());
        PaginationDto pagination = new PaginationDto(page, size, meetingPage.getTotalElements(), meetingPage.getTotalPages());
        return ApiResponse.success(items, pagination);
    }

    @GetMapping("/{id}")
    public ApiResponse<MeetingResponse> getMeeting(@PathVariable Long id) {
        return ApiResponse.success(meetingService.getMeeting(id));
    }

    @PostMapping
    public ApiResponse<MeetingResponse> createMeeting(@RequestBody CreateMeetingRequest request) {
        return ApiResponse.success(meetingService.createMeeting(request));
    }

    @PostMapping("/{id}/review")
    public ApiResponse<MeetingResponse> reviewMeeting(@PathVariable Long id, @RequestBody ReviewRequest request) {
        return ApiResponse.success(meetingService.reviewMeeting(id, request));
    }

    @PostMapping("/{id}/publish")
    public ApiResponse<MeetingResponse> publishMeeting(@PathVariable Long id) {
        return ApiResponse.success(meetingService.publishMeeting(id));
    }

    @PostMapping("/{id}/archive")
    public ApiResponse<MeetingResponse> archiveMeeting(@PathVariable Long id) {
        return ApiResponse.success(meetingService.archiveMeeting(id));
    }
}
