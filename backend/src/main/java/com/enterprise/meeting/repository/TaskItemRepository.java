package com.enterprise.meeting.repository;

import com.enterprise.meeting.entity.TaskItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TaskItemRepository extends JpaRepository<TaskItem, Long> {
    List<TaskItem> findByMeetingIdOrderByCreatedAtAsc(Long meetingId);

    Page<TaskItem> findByAssignedToIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<TaskItem> findByAssignedToIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT t FROM TaskItem t WHERE t.assignedTo.id = :userId AND t.state IN :states AND t.deletedAt IS NULL ORDER BY t.dueDate ASC")
    List<TaskItem> findByUserAndStates(@Param("userId") Long userId, @Param("states") List<String> states);

    @Query("SELECT t FROM TaskItem t WHERE t.meeting.id = :meetingId AND t.deletedAt IS NULL ORDER BY t.createdAt ASC")
    List<TaskItem> findActiveByMeeting(@Param("meetingId") Long meetingId);

    long countByMeetingIdAndState(Long meetingId, String state);
}
