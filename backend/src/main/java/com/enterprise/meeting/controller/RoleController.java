package com.enterprise.meeting.controller;

import com.enterprise.meeting.dto.ApiResponse;
import com.enterprise.meeting.dto.RoleDto;
import com.enterprise.meeting.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ApiResponse<List<RoleDto>> getAllRoles() {
        return ApiResponse.success(roleService.getAllRoles());
    }
}
