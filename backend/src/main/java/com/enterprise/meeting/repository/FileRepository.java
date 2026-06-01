package com.enterprise.meeting.repository;

import com.enterprise.meeting.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByMeetingIdOrderByCreatedAtDesc(Long meetingId);
    List<FileEntity> findByTaskIdOrderByCreatedAtDesc(Long taskId);
}
