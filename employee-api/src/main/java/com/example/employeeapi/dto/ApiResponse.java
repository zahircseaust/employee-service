package com.example.employeeapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/** Standard success envelope: { success, status, message, path, data, pagination?, timestamp }. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        int status,
        String message,
        String path,
        T data,
        Pagination pagination,
        Instant timestamp
) {
    public record Pagination(int page, int size, long totalElements, int totalPages,
                             boolean first, boolean last, List<String> sort) {}

    public static <T> ApiResponse<T> ok(int status, String message, String path, T data) {
        return new ApiResponse<>(true, status, message, path, data, null, Instant.now());
    }

    public static <T> ApiResponse<List<T>> ok(int status, String message, String path, PageResponse<T> p) {
        return new ApiResponse<>(true, status, message, path, p.content(),
                new Pagination(p.page(), p.size(), p.totalElements(), p.totalPages(),
                        p.first(), p.last(), p.sort()),
                Instant.now());
    }
}
