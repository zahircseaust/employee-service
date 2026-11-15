package com.example.employeeapi.dto;

import com.example.employeeapi.entity.EmployeeStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Body for POST /api/employees */
public record CreateEmployeeRequest(

        @NotBlank(message = "employeeCode is required")
        @Size(max = 20, message = "employeeCode must be at most 20 characters")
        String employeeCode,

        @NotBlank(message = "firstName is required")
        @Size(max = 50, message = "firstName must be at most 50 characters")
        String firstName,

        @NotBlank(message = "lastName is required")
        @Size(max = 50, message = "lastName must be at most 50 characters")
        String lastName,

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        String email,

        @Pattern(regexp = "^01[3-9]\\d{8}$", message = "phone must be a valid 11-digit BD number, e.g. 01712345678")
        String phone,

        @NotBlank(message = "department is required")
        String department,

        @Size(max = 100, message = "designation must be at most 100 characters")
        String designation,

        /** Optional. Defaults to ACTIVE. */
        EmployeeStatus status,

        @NotNull(message = "joinDate is required")
        @PastOrPresent(message = "joinDate cannot be in the future")
        LocalDate joinDate,

        /** Accepted on create, but never returned by any API. */
        @PositiveOrZero(message = "salary cannot be negative")
        BigDecimal salary
) {}
