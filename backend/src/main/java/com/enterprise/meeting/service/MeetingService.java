package com.enterprise.meeting.service;

import com.enterprise.meeting.dto.*;
import com.enterprise.meeting.entity.Meeting;
import com.enterprise.meeting.entity.User;
import com.enterprise.meeting.exception.BusinessException;
import com.enterprise.meeting.repository.MeetingRepository;
import com.enterprise.meeting.repository.UserRepository;
import com.enterprise.meeting.security.SecurityUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final ObjectMapper objectMapper;

    public Page<Meeting> getMeetingsPage(Long userId, int page, int size) {
        securityUtil.getCurrentUser();
        if (userId != null) {
            return meetingRepository.findByUserIdPaged(userId, PageRequest.of(page, size));
        }
        return meetingRepository.findAllActivePaged(PageRequest.of(page, size));
    }

    public MeetingResponse getMeeting(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "会议不存在"));
        return toResponse(meeting);
    }

    @Transactional
    public MeetingResponse createMeeting(CreateMeetingRequest request) {
        User creator = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new BusinessException(400, "创建者不存在"));

        Meeting meeting = Meeting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .meetingTime(request.getMeetingTime() != null
                        ? LocalDateTime.parse(request.getMeetingTime(), DateTimeFormatter.ISO_DATE_TIME)
                        : null)
                .location(request.getLocation())
                .durationMinutes(request.getDurationMinutes())
                .createdBy(creator)
                .assignedReviewer(request.getAssignedReviewer() != null
                        ? userRepository.findById(request.getAssignedReviewer()).orElse(null)
                        : null)
                .state("PENDING")
                .aiRetryCount(0)
                .build();

        meeting = meetingRepository.save(meeting);
        return toResponse(meeting);
    }

    @Transactional
    public MeetingResponse reviewMeeting(Long id, ReviewRequest request) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "会议不存在"));

        if (!"PENDING_REVIEW".equals(meeting.getState())) {
            throw new BusinessException(400, "当前状态不允许审核");
        }

        if (Boolean.TRUE.equals(request.getApproved())) {
            meeting.setState("PENDING_PUBLISH");
        } else {
            meeting.setState("PENDING");
            meeting.setAiRetryCount(meeting.getAiRetryCount() + 1);
            meeting.setAiError(request.getComment());
        }

        meeting = meetingRepository.save(meeting);
        return toResponse(meeting);
    }

    @Transactional
    public MeetingResponse publishMeeting(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "会议不存在"));

        if (!"PENDING_PUBLISH".equals(meeting.getState())) {
            throw new BusinessException(400, "当前状态不允许发布");
        }

        meeting.setState("COMPLETED");
        meeting = meetingRepository.save(meeting);
        return toResponse(meeting);
    }

    @Transactional
    public MeetingResponse archiveMeeting(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "会议不存在"));

        if (!"COMPLETED".equals(meeting.getState())) {
            throw new BusinessException(400, "当前状态不允许归档");
        }

        meeting.setState("ARCHIVED");
        meeting = meetingRepository.save(meeting);
        return toResponse(meeting);
    }

    public List<MeetingResponse> toResponseList(List<Meeting> meetings) {
        return meetings.stream().map(this::toResponse).toList();
    }

    private MeetingResponse toResponse(Meeting meeting) {
        Map<String, String> metadata = parseStateMetadata(meeting.getStateMetadata());
        return new MeetingResponse(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getDescription(),
                meeting.getMeetingTime() != null ? meeting.getMeetingTime().toString() : null,
                meeting.getLocation(),
                meeting.getDurationMinutes(),
                meeting.getCreatedBy().getId(),
                meeting.getAssignedReviewer() != null ? meeting.getAssignedReviewer().getId() : null,
                meeting.getState(),
                metadata,
                meeting.getAiRetryCount(),
                meeting.getAiError(),
                meeting.getCreatedAt() != null ? meeting.getCreatedAt().toString() : null,
                meeting.getUpdatedAt() != null ? meeting.getUpdatedAt().toString() : null
        );
    }

    private Map<String, String> parseStateMetadata(String json) {
        if (json == null || json.isBlank()) return Collections.emptyMap();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
