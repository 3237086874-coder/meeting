package com.enterprise.meeting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private long timestamp;
    private String requestId;
    private PaginationDto pagination;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data, System.currentTimeMillis(), UUID.randomUUID().toString().replace("-", ""), null);
    }

    public static <T> ApiResponse<T> success(T data, PaginationDto pagination) {
        return new ApiResponse<>(200, "success", data, System.currentTimeMillis(), UUID.randomUUID().toString().replace("-", ""), pagination);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null, System.currentTimeMillis(), UUID.randomUUID().toString().replace("-", ""), null);
    }
}
