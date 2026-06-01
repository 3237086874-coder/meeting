package com.enterprise.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginationDto {
    private int page;
    private int size;
    private long total;
    private int totalPages;
}
