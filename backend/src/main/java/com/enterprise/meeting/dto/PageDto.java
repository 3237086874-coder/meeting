package com.enterprise.meeting.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageDto<T> {
    private List<T> items;
    private long total;
    private int page;
    private int size;
    private int totalPages;

    public PaginationDto toPagination() {
        return new PaginationDto(page, size, total, totalPages);
    }
}
