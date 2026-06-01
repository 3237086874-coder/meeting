package com.enterprise.meeting.controller;

import com.enterprise.meeting.dto.*;
import com.enterprise.meeting.entity.TaskItem;
import com.enterprise.meeting.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ApiResponse<List<TaskResponse>> getTasks(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<TaskItem> taskPage = taskService.getTasksPage(userId, page, size);
        List<TaskResponse> items = taskService.toResponseList(taskPage.getContent());
        PaginationDto pagination = new PaginationDto(page, size, taskPage.getTotalElements(), taskPage.getTotalPages());
        return ApiResponse.success(items, pagination);
    }

    @GetMapping("/{id}")
    public ApiResponse<TaskResponse> getTask(@PathVariable Long id) {
        return ApiResponse.success(taskService.getTask(id));
    }

    @PostMapping("/{id}/confirm")
    public ApiResponse<TaskResponse> confirmTask(@PathVariable Long id) {
        return ApiResponse.success(taskService.confirmTask(id));
    }

    @PostMapping("/{id}/progress")
    public ApiResponse<TaskResponse> updateProgress(@PathVariable Long id, @RequestBody TaskActionRequest request) {
        return ApiResponse.success(taskService.updateProgress(id, request));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<TaskResponse> completeTask(@PathVariable Long id, @RequestBody TaskActionRequest request) {
        return ApiResponse.success(taskService.completeTask(id, request));
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<TaskResponse> rejectTask(@PathVariable Long id, @RequestBody TaskActionRequest request) {
        return ApiResponse.success(taskService.rejectTask(id, request));
    }
}
