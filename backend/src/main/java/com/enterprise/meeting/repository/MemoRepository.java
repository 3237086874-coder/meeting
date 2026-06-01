package com.enterprise.meeting.repository;

import com.enterprise.meeting.entity.Memo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Memo> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT m FROM Memo m WHERE m.user.id = :userId AND m.isCompleted = false AND m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    List<Memo> findIncompleteByUser(@Param("userId") Long userId);
}
