package com.enterprise.meeting.controller;

import com.enterprise.meeting.dto.*;
import com.enterprise.meeting.entity.Memo;
import com.enterprise.meeting.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @GetMapping
    public ApiResponse<List<MemoResponse>> getMemos(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Memo> memoPage = memoService.getMemosPage(userId, page, size);
        List<MemoResponse> items = memoService.toResponseList(memoPage.getContent());
        PaginationDto pagination = new PaginationDto(page, size, memoPage.getTotalElements(), memoPage.getTotalPages());
        return ApiResponse.success(items, pagination);
    }

    @GetMapping("/{id}")
    public ApiResponse<MemoResponse> getMemo(@PathVariable Long id) {
        return ApiResponse.success(memoService.getMemo(id));
    }

    @PostMapping
    public ApiResponse<MemoResponse> createMemo(@RequestBody CreateMemoRequest request) {
        return ApiResponse.success(memoService.createMemo(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<MemoResponse> updateMemo(@PathVariable Long id, @RequestBody MemoRequest request) {
        return ApiResponse.success(memoService.updateMemo(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMemo(@PathVariable Long id) {
        memoService.deleteMemo(id);
        return ApiResponse.<Void>success(null);
    }
}
