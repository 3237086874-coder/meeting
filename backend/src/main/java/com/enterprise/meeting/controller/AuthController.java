package com.enterprise.meeting.controller;

import com.enterprise.meeting.dto.ApiResponse;
import com.enterprise.meeting.dto.LoginRequest;
import com.enterprise.meeting.dto.LoginResponse;
import com.enterprise.meeting.dto.RefreshTokenRequest;
import com.enterprise.meeting.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            return ApiResponse.success(authService.login(request));
        } catch (RuntimeException e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            return ApiResponse.success(authService.refresh(request.getRefreshToken()));
        } catch (RuntimeException e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }
}
