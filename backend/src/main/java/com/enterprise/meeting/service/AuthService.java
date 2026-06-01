package com.enterprise.meeting.service;

import com.enterprise.meeting.dto.LoginRequest;
import com.enterprise.meeting.dto.LoginResponse;
import com.enterprise.meeting.entity.User;
import com.enterprise.meeting.repository.UserRepository;
import com.enterprise.meeting.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!user.getIsActive()) {
            throw new RuntimeException("账号已被禁用");
        }

        return buildLoginResponse(user);
    }

    public LoginResponse refresh(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new RuntimeException("刷新令牌无效或已过期");
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!user.getIsActive()) {
            throw new RuntimeException("账号已被禁用");
        }

        return buildLoginResponse(user);
    }

    private LoginResponse buildLoginResponse(User user) {
        String roleCode = user.getRole() != null ? user.getRole().getCode() : "STAFF";
        String accessToken = jwtUtil.generateToken(user.getUsername(), roleCode);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        return new LoginResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                List.of(roleCode)
        );
    }
}
