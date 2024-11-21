package com.stc.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DepartmentValidationService {

    private static final List<String> ALLOWED_DEPARTMENTS = Arrays.asList("HR", "Engineering", "Sales", "Marketing", "Finance");


    public boolean validateDepartment(String department) {
        if (department == null || department.isBlank()) {
            return false;
        }

        if (!ALLOWED_DEPARTMENTS.contains(department)) {
            return false;
        }
        return true;
    }
}