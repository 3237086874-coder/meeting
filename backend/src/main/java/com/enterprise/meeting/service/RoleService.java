package com.enterprise.meeting.service;

import com.enterprise.meeting.dto.RoleDto;
import com.enterprise.meeting.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleDto(role.getId(), role.getCode(), role.getName()))
                .toList();
    }
}
