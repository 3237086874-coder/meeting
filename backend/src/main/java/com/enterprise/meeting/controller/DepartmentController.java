package com.enterprise.meeting.controller;

import com.enterprise.meeting.dto.ApiResponse;
import com.enterprise.meeting.dto.DepartmentResponse;
import com.enterprise.meeting.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ApiResponse<List<DepartmentResponse>> getAllDepartments() {
        return ApiResponse.success(departmentService.getAllDepartments());
    }
}
