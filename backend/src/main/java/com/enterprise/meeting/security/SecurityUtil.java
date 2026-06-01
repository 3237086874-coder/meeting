package com.enterprise.meeting.security;

import com.enterprise.meeting.entity.User;
import com.enterprise.meeting.exception.BusinessException;
import com.enterprise.meeting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException(401, "未登录");
        }
        return auth.getName();
    }

    public User getCurrentUser() {
        String username = getCurrentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(401, "用户不存在"));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
