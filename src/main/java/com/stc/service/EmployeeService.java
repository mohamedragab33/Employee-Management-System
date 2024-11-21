package com.stc.service;

import com.stc.dto.EmployeeReq;
import com.stc.dto.EmployeeRes;
import com.stc.entity.Employee;
import com.stc.exception.EmployeeNotFoundException;
import com.stc.exception.InvalidInputException;
import com.stc.mapper.EmployeeMapper;
import com.stc.repository.EmployeeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    public static final String EMPLOYEE_NOT_FOUND_WITH_ID = "Employee not found with ID: ";
    private final EmployeeRepository employeeRepository;
    private final EmailValidationService emailValidationService;
    private final DepartmentValidationService departmentValidationService;
    private final EmailService emailService;
    private final EmployeeMapper employeeMapper;

    public EmployeeRes createEmployee(@Valid EmployeeReq employeeReq) {
        validateEmail(employeeReq);
        validateDepartment(employeeReq);

        Employee employee = employeeMapper.toEntity(employeeReq);
        employee = employeeRepository.save(employee);

        // Send Email Notification
        emailService.sendEmployeeCreationNotification(employee);

        // Map Entity to Response
        return employeeMapper.toEmployeeRes(employee);
    }

    private void validateEmail(EmployeeReq employeeReq) {
        boolean isEmailValid = emailValidationService.validateEmail(employeeReq.email());
        if (!isEmailValid) {
            throw new InvalidInputException("Invalid email address");
        }
    }

    private void validateDepartment(EmployeeReq employeeReq) {
        boolean isDepartmentValid = departmentValidationService.validateDepartment(employeeReq.department());
        if (!isDepartmentValid) {
            throw new InvalidInputException("Invalid department");
        }
    }

    public EmployeeRes getEmployeeById(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_WITH_ID + id));
        return employeeMapper.toEmployeeRes(employee);
    }

    public EmployeeRes updateEmployee(UUID id, @Valid EmployeeReq employeeReq) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_WITH_ID + id));

        // Update fields
        employee.setFirstName(employeeReq.firstName());
        employee.setLastName(employeeReq.lastName());
        employee.setEmail(employeeReq.email());
        employee.setDepartment(employeeReq.department());
        employee.setSalary(employeeReq.salary());

        // Save updated employee
        employee = employeeRepository.save(employee);
        return employeeMapper.toEmployeeRes(employee);
    }

    public void deleteEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_WITH_ID + id));
        employeeRepository.delete(employee);
    }

    public List<EmployeeRes> listAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toEmployeeRes)
                .collect(Collectors.toList());
    }

}
