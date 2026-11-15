package com.example.employeeapi.service;

import com.example.employeeapi.dto.CreateEmployeeRequest;
import com.example.employeeapi.dto.EmployeeDto;
import com.example.employeeapi.dto.EmployeeFilter;
import com.example.employeeapi.dto.PageResponse;
import com.example.employeeapi.entity.Employee;
import com.example.employeeapi.entity.EmployeeStatus;
import com.example.employeeapi.exception.BadRequestException;
import com.example.employeeapi.exception.ConflictException;
import com.example.employeeapi.exception.ResourceNotFoundException;
import com.example.employeeapi.repository.EmployeeRepository;
import com.example.employeeapi.specification.EmployeeSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    /** Sort whitelist: API field name -> entity field(s). */
    private static final Map<String, List<String>> SORT_FIELDS = new LinkedHashMap<>();
    static {
        SORT_FIELDS.put("name", List.of("firstName", "lastName"));
        SORT_FIELDS.put("joinDate", List.of("joinDate"));
        SORT_FIELDS.put("department", List.of("department"));
        SORT_FIELDS.put("employeeCode", List.of("employeeCode"));
        SORT_FIELDS.put("status", List.of("status"));
    }

    private final EmployeeRepository employeeRepository;

    /** Get all + get by id + search + filter + pagination, in one method. */
    public PageResponse<EmployeeDto> getEmployees(EmployeeFilter filter, Pageable pageable) {
        Specification<Employee> spec = Specification
                .where(EmployeeSpecs.idEquals(filter.getId()))
                .and(EmployeeSpecs.search(filter.getSearch()))
                .and(EmployeeSpecs.department(filter.getDepartment()))
                .and(EmployeeSpecs.statusIn(filter.getStatus()))
                .and(EmployeeSpecs.joinedFrom(filter.getJoinedFrom()))
                .and(EmployeeSpecs.joinedTo(filter.getJoinedTo()));

        PageResponse<EmployeeDto> result = findPage(spec, pageable);

        // ?id= for an employee that does not exist at all → 404, like a normal get-by-id
        if (filter.getId() != null && result.totalElements() == 0 && !employeeRepository.existsById(filter.getId())) {
            throw new ResourceNotFoundException("Employee " + filter.getId() + " not found");
        }
        return result;
    }

    // Create
    @Transactional
    public EmployeeDto create(CreateEmployeeRequest request) {
        String email = request.email().trim().toLowerCase();
        String code = request.employeeCode().trim().toUpperCase();

        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An employee with email '" + email + "' already exists");
        }
        if (employeeRepository.existsByEmployeeCodeIgnoreCase(code)) {
            throw new ConflictException("An employee with code '" + code + "' already exists");
        }

        Employee employee = new Employee();
        employee.setEmployeeCode(code);
        employee.setFirstName(request.firstName().trim());
        employee.setLastName(request.lastName().trim());
        employee.setEmail(email);
        employee.setPhone(request.phone());
        employee.setDepartment(request.department().trim());
        employee.setDesignation(request.designation());
        employee.setStatus(request.status() != null ? request.status() : EmployeeStatus.ACTIVE);
        employee.setJoinDate(request.joinDate());
        employee.setSalary(request.salary());

        return toDto(employeeRepository.save(employee));
    }

    // ---------- helpers ----------

    private PageResponse<EmployeeDto> findPage(Specification<Employee> spec, Pageable pageable) {
        Pageable safePageable = PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(), toEntitySort(pageable.getSort()));

        Page<EmployeeDto> page = employeeRepository.findAll(spec, safePageable).map(this::toDto);

        List<String> appliedSort = pageable.getSort().stream()
                .map(o -> o.getProperty() + "," + o.getDirection().name().toLowerCase())
                .toList();
        return PageResponse.of(page, appliedSort);
    }

    private Sort toEntitySort(Sort requested) {
        List<Sort.Order> orders = new ArrayList<>();
        for (Sort.Order order : requested) {
            List<String> entityFields = SORT_FIELDS.get(order.getProperty());
            if (entityFields == null) {
                throw new BadRequestException("Sorting by '" + order.getProperty()
                        + "' is not allowed. Allowed: " + String.join(", ", SORT_FIELDS.keySet()));
            }
            entityFields.forEach(f -> orders.add(new Sort.Order(order.getDirection(), f)));
        }
        orders.add(Sort.Order.asc("id")); // stable order between pages
        return Sort.by(orders);
    }

    private EmployeeDto toDto(Employee e) {
        return EmployeeDto.builder()
                .id(e.getId())
                .employeeCode(e.getEmployeeCode())
                .fullName(e.getFirstName() + " " + e.getLastName())
                .email(e.getEmail())
                .phone(e.getPhone())
                .department(e.getDepartment())
                .designation(e.getDesignation())
                .status(e.getStatus())
                .joinDate(e.getJoinDate())
                .build();
    }
}
