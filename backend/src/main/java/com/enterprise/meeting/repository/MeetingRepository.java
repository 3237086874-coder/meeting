package com.enterprise.meeting.repository;

import com.enterprise.meeting.entity.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByCreatedByIdOrderByCreatedAtDesc(Long userId);

    List<Meeting> findByStateOrderByCreatedAtDesc(String state);

    @Query("SELECT m FROM Meeting m WHERE m.assignedReviewer.id = :userId AND m.state = :state ORDER BY m.createdAt DESC")
    List<Meeting> findByReviewerAndState(@Param("userId") Long userId, @Param("state") String state);

    @Query("SELECT m FROM Meeting m WHERE m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    List<Meeting> findAllActive();

    @Query("SELECT m FROM Meeting m WHERE m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    Page<Meeting> findAllActivePaged(Pageable pageable);

    @Query("SELECT m FROM Meeting m WHERE m.createdBy.id = :userId AND m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    Page<Meeting> findByUserIdPaged(@Param("userId") Long userId, Pageable pageable);
}
