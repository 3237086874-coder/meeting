package com.enterprise.meeting.service;

import com.enterprise.meeting.dto.*;
import com.enterprise.meeting.entity.TaskItem;
import com.enterprise.meeting.exception.BusinessException;
import com.enterprise.meeting.repository.TaskItemRepository;
import com.enterprise.meeting.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskItemRepository taskRepository;
    private final SecurityUtil securityUtil;

    public Page<TaskItem> getTasksPage(Long userId, int page, int size) {
        securityUtil.getCurrentUser();
        return taskRepository.findByAssignedToIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
    }

    public TaskResponse getTask(Long id) {
        TaskItem task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "任务不存在"));
        return toResponse(task);
    }

    public TaskResponse confirmTask(Long id) {
        TaskItem task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "任务不存在"));
        if (!"PENDING_CONFIRM".equals(task.getState())) {
            throw new BusinessException(400, "当前状态不允许确认");
        }
        task.setState("EXECUTING");
        task = taskRepository.save(task);
        return toResponse(task);
    }

    public TaskResponse updateProgress(Long id, TaskActionRequest request) {
        TaskItem task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "任务不存在"));
        if (!"EXECUTING".equals(task.getState())) {
            throw new BusinessException(400, "当前状态不能更新进度");
        }
        task.setCompletionNote(request.getNote());
        task = taskRepository.save(task);
        return toResponse(task);
    }

    public TaskResponse completeTask(Long id, TaskActionRequest request) {
        TaskItem task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "任务不存在"));
        if (!"EXECUTING".equals(task.getState())) {
            throw new BusinessException(400, "当前状态不允许完成");
        }
        task.setState("COMPLETED");
        task.setCompletionNote(request.getNote());
        task.setCompletedAt(LocalDateTime.now());
        task = taskRepository.save(task);
        return toResponse(task);
    }

    public TaskResponse rejectTask(Long id, TaskActionRequest request) {
        TaskItem task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "任务不存在"));
        if (!"EXECUTING".equals(task.getState())) {
            throw new BusinessException(400, "当前状态不允许驳回");
        }
        task.setState("REJECTED");
        task.setCompletionNote(request.getNote());
        task.setCompletedAt(LocalDateTime.now());
        task = taskRepository.save(task);
        return toResponse(task);
    }

    public List<TaskResponse> toResponseList(List<TaskItem> tasks) {
        return tasks.stream().map(this::toResponse).toList();
    }

    private TaskResponse toResponse(TaskItem task) {
        return new TaskResponse(
                task.getId(),
                task.getMeeting().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getAssignedTo().getId(),
                task.getAssignedBy() != null ? task.getAssignedBy().getId() : null,
                task.getPriority(),
                task.getDueDate() != null ? task.getDueDate().toString() : null,
                task.getState(),
                task.getCompletionNote(),
                task.getIsAiExtracted(),
                task.getIsVisible(),
                task.getCreatedAt() != null ? task.getCreatedAt().toString() : null,
                task.getUpdatedAt() != null ? task.getUpdatedAt().toString() : null,
                task.getCompletedAt() != null ? task.getCompletedAt().toString() : null
        );
    }
}
