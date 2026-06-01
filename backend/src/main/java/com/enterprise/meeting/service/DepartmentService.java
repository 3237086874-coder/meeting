package com.enterprise.meeting.service;

import com.enterprise.meeting.dto.DepartmentResponse;
import com.enterprise.meeting.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(dept -> new DepartmentResponse(dept.getId(), dept.getName(), dept.getDescription()))
                .toList();
    }
}
