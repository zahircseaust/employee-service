package com.example.employeeapi.controller;

import com.example.employeeapi.dto.ApiResponse;
import com.example.employeeapi.dto.CreateEmployeeRequest;
import com.example.employeeapi.dto.EmployeeDto;
import com.example.employeeapi.dto.EmployeeFilter;
import com.example.employeeapi.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * ONE endpoint for get all, get by id, search, filter and pagination.
     *
     *   GET /api/employees                                  → get all (paginated)
     *   GET /api/employees?id=5                             → get by id
     *   GET /api/employees?search=rahim                     → search
     *   GET /api/employees?department=IT&status=ACTIVE      → filter
     *   GET /api/employees?page=1&size=10&sort=joinDate,desc → pagination + sort
     *   GET /api/employees?search=a&department=IT&page=0&size=5 → all combined
     */
    @GetMapping
    public ApiResponse<List<EmployeeDto>> getEmployees(
            @Valid @ModelAttribute EmployeeFilter filter,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            HttpServletRequest req) {
        return ApiResponse.ok(200, "Employees retrieved successfully", req.getRequestURI(),
                employeeService.getEmployees(filter, pageable));
    }

    /** Create: POST /api/employees → 201 Created */
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeDto>> create(@Valid @RequestBody CreateEmployeeRequest request,
                                                             HttpServletRequest req) {
        EmployeeDto created = employeeService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .queryParam("id", created.id())
                .build()
                .toUri();
        return ResponseEntity.created(location)
                .body(ApiResponse.ok(201, "Employee created successfully", req.getRequestURI(), created));
    }
}
