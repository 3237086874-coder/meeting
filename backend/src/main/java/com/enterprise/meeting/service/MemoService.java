package com.enterprise.meeting.service;

import com.enterprise.meeting.dto.*;
import com.enterprise.meeting.entity.Memo;
import com.enterprise.meeting.exception.BusinessException;
import com.enterprise.meeting.repository.MemoRepository;
import com.enterprise.meeting.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final SecurityUtil securityUtil;

    public Page<Memo> getMemosPage(Long userId, int page, int size) {
        securityUtil.getCurrentUser();
        return memoRepository
                .findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
    }

    public MemoResponse getMemo(Long id) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "备忘不存在"));
        return toResponse(memo);
    }

    public MemoResponse createMemo(CreateMemoRequest request) {
        var user = securityUtil.getCurrentUser();

        Memo memo = Memo.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .priority(request.getPriority() != null ? request.getPriority() : "NORMAL")
                .color(request.getColor())
                .reminderAt(request.getReminderAt() != null
                        ? LocalDateTime.parse(request.getReminderAt(), DateTimeFormatter.ISO_DATE_TIME)
                        : null)
                .build();

        memo = memoRepository.save(memo);
        return toResponse(memo);
    }

    public MemoResponse updateMemo(Long id, MemoRequest request) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "备忘不存在"));

        if (request.getTitle() != null) memo.setTitle(request.getTitle());
        if (request.getContent() != null) memo.setContent(request.getContent());
        if (request.getPriority() != null) memo.setPriority(request.getPriority());
        if (request.getColor() != null) memo.setColor(request.getColor());
        if (request.getIsCompleted() != null) memo.setIsCompleted(request.getIsCompleted());
        if (request.getReminderAt() != null) {
            memo.setReminderAt(LocalDateTime.parse(request.getReminderAt(), DateTimeFormatter.ISO_DATE_TIME));
        }

        memo = memoRepository.save(memo);
        return toResponse(memo);
    }

    public void deleteMemo(Long id) {
        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "备忘不存在"));
        memo.setDeletedAt(LocalDateTime.now());
        memoRepository.save(memo);
    }

    public List<MemoResponse> toResponseList(List<Memo> memos) {
        return memos.stream().map(this::toResponse).toList();
    }

    private MemoResponse toResponse(Memo memo) {
        return new MemoResponse(
                memo.getId(),
                memo.getUser().getId(),
                memo.getTitle(),
                memo.getContent(),
                memo.getPriority(),
                memo.getReminderAt() != null ? memo.getReminderAt().toString() : null,
                memo.getIsCompleted(),
                memo.getColor(),
                memo.getCreatedAt() != null ? memo.getCreatedAt().toString() : null,
                memo.getUpdatedAt() != null ? memo.getUpdatedAt().toString() : null
        );
    }
}
