package com.example.employeeapi.specification;

import com.example.employeeapi.entity.Employee;
import com.example.employeeapi.entity.EmployeeStatus;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

/**
 * Each method returns null when its value is empty,
 * and Spring Data simply skips null specifications.
 */
public final class EmployeeSpecs {

    private EmployeeSpecs() {}

    public static Specification<Employee> idEquals(Long id) {
        if (id == null) return null;
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    public static Specification<Employee> search(String search) {
        if (search == null || search.isBlank()) return null;
        String pattern = "%" + escapeLike(search.trim().toLowerCase()) + "%";

        return (root, query, cb) -> {
            Expression<String> fullName = cb.lower(
                    cb.concat(cb.concat(root.get("firstName"), " "), root.get("lastName")));
            return cb.or(
                    cb.like(fullName, pattern, '\\'),
                    cb.like(cb.lower(root.get("email")), pattern, '\\'),
                    cb.like(cb.lower(root.get("employeeCode")), pattern, '\\')
            );
        };
    }

    public static Specification<Employee> department(String department) {
        if (department == null || department.isBlank()) return null;
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("department")), department.trim().toLowerCase());
    }

    public static Specification<Employee> statusIn(List<EmployeeStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) return null;
        return (root, query, cb) -> root.get("status").in(statuses);
    }

    public static Specification<Employee> joinedFrom(LocalDate from) {
        if (from == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("joinDate"), from);
    }

    public static Specification<Employee> joinedTo(LocalDate to) {
        if (to == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("joinDate"), to);
    }

    /** Stop users' % and _ from acting as SQL wildcards. */
    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
