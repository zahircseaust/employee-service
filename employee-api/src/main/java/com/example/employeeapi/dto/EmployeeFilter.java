package com.example.employeeapi.dto;

import com.example.employeeapi.entity.EmployeeStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * All query parameters for GET /api/employees. Every one is optional:
 * none = get all, id = get by id, search = search, the rest = filters.
 */
@Data
public class EmployeeFilter {

    /** Get by id: ?id=5 */
    @Positive(message = "id must be a positive number")
    private Long id;

    /** Search full name, email or employee code: ?search=rahim */
    @Size(max = 100, message = "search must be at most 100 characters")
    private String search;

    /** ?department=IT (case-insensitive) */
    private String department;

    /** ?status=ACTIVE or ?status=ACTIVE,ON_LEAVE */
    private List<EmployeeStatus> status;

    /** ?joinedFrom=2022-01-01 */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate joinedFrom;

    /** ?joinedTo=2023-12-31 */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate joinedTo;

    @AssertTrue(message = "joinedFrom must be on or before joinedTo")
    public boolean isDateRangeValid() {
        return joinedFrom == null || joinedTo == null || !joinedFrom.isAfter(joinedTo);
    }
}
