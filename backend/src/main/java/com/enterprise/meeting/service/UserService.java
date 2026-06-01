package com.enterprise.meeting.service;

import com.enterprise.meeting.dto.*;
import com.enterprise.meeting.entity.Department;
import com.enterprise.meeting.entity.Role;
import com.enterprise.meeting.entity.User;
import com.enterprise.meeting.exception.BusinessException;
import com.enterprise.meeting.repository.DepartmentRepository;
import com.enterprise.meeting.repository.RoleRepository;
import com.enterprise.meeting.repository.UserRepository;
import com.enterprise.meeting.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final SecurityUtil securityUtil;
    private final PasswordEncoder passwordEncoder;

    public Page<User> getUsersPage(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    public List<UserResponse> getUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size))
                .stream().map(this::toResponse).toList();
    }

    public long getUserCount() {
        return userRepository.count();
    }

    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        return toResponse(user);
    }

    public UserResponse getCurrentUser() {
        User user = securityUtil.getCurrentUser();
        return toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        if (request.getDisplayName() != null) user.setDisplayName(request.getDisplayName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getTitle() != null) user.setTitle(request.getTitle());
        if (request.getIsActive() != null) user.setIsActive(request.getIsActive());
        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new BusinessException(400, "角色不存在"));
            user.setRole(role);
        }
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new BusinessException(400, "部门不存在"));
            user.setDepartment(dept);
        }

        user = userRepository.save(user);
        return toResponse(user);
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream().map(this::toResponse).toList();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getPhone(),
                user.getEmail(),
                user.getDepartment() != null ? user.getDepartment().getId() : null,
                user.getDepartment() != null ? user.getDepartment().getName() : null,
                user.getTitle(),
                user.getIsActive(),
                user.getRole() != null
                        ? List.of(new RoleDto(user.getRole().getId(), user.getRole().getCode(), user.getRole().getName()))
                        : List.of()
        );
    }
}
