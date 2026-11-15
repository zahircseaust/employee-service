package com.example.employeeapi.dto;

import com.example.employeeapi.entity.EmployeeStatus;
import lombok.Builder;

import java.time.LocalDate;

/** What the employee table needs. No salary or other sensitive fields. */
@Builder
public record EmployeeDto(
        Long id,
        String employeeCode,
        String fullName,
        String email,
        String phone,
        String department,
        String designation,
        EmployeeStatus status,
        LocalDate joinDate
) {}
