package com.example.employeeapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/** Standard error envelope: { success:false, status, error, message, path, errors?, timestamp }. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        boolean success,
        int status,
        String error,
        String message,
        String path,
        List<FieldIssue> errors,
        Instant timestamp
) {
    /** One validation problem, e.g. {"field":"email","message":"email is required"}. */
    public record FieldIssue(String field, String message) {}
}
