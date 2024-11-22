package com.stc.service;

import com.stc.constants.LogMessages;
import com.stc.dto.EmployeeReq;
import com.stc.dto.EmployeeRes;
import com.stc.entity.Employee;
import com.stc.exception.EmployeeNotFoundException;
import com.stc.exception.InvalidInputException;
import com.stc.mapper.EmployeeMapper;
import com.stc.repository.EmployeeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.stc.constants.LogMessages.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmployeeService {

    public static final String EMPLOYEE_NOT_FOUND_WITH_ID = "Employee not found with ID: ";
    private final EmployeeRepository employeeRepository;
    private final EmailValidationService emailValidationService;
    private final DepartmentValidationService departmentValidationService;
    private final EmailService emailService;
    private final EmployeeMapper employeeMapper;

    public EmployeeRes createEmployee(@Valid EmployeeReq employeeReq) {
        log.info(LogMessages.EMPLOYEE_CREATION_START, employeeReq);

        validateEmail(employeeReq.email());
        validateDepartment(employeeReq.department());

        Employee employee = employeeMapper.toEntity(employeeReq);
        employee = employeeRepository.save(employee);

        log.info(EMPLOYEE_CREATION_SUCCESS, employee.getId());

        emailService.sendEmployeeCreationNotification(employee);
        log.debug(EMAIL_NOTIFICATION_SENT, employee.getId());

        return employeeMapper.toEmployeeRes(employee);
    }

    private void validateEmail(String email) {
        log.debug(EMAIL_VALIDATION_START, email);
        boolean isEmailValid = emailValidationService.validateEmail(email);
        if (!isEmailValid) {
            log.warn(EMAIL_VALIDATION_FAILURE, email);
            throw new InvalidInputException("Invalid email address");
        }
        log.debug(EMAIL_VALIDATION_SUCCESS, email);
    }

    private void validateDepartment(String department) {
        log.debug(DEPARTMENT_VALIDATION_START, department);
        boolean isDepartmentValid = departmentValidationService.validateDepartment(department);
        if (!isDepartmentValid) {
            log.warn(DEPARTMENT_VALIDATION_FAILURE, department);
            throw new InvalidInputException("Invalid department");
        }
        log.debug(DEPARTMENT_VALIDATION_SUCCESS, department);
    }

    public EmployeeRes getEmployeeById(UUID id) {
        log.info(FETCH_EMPLOYEE_START, id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(EMPLOYEE_NOT_FOUND, id);
                    return new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_WITH_ID + id);
                });
        log.info(FETCH_EMPLOYEE_SUCCESS, id);
        return employeeMapper.toEmployeeRes(employee);
    }

    public EmployeeRes updateEmployee(UUID id, @Valid EmployeeReq employeeReq) {
        log.info(EMPLOYEE_UPDATE_START, id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(EMPLOYEE_NOT_FOUND, id);
                    return new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_WITH_ID + id);
                });

        log.debug(UPDATING_EMPLOYEE_DETAILS_WITH_NEW_DATA, employeeReq);
        if (!employee.getEmail().equals(employeeReq.email())) {
            validateEmail(employeeReq.email());
        }
        if (!employee.getDepartment().equals(employeeReq.department())) {
            validateDepartment(employeeReq.department());
        }

        employee.setFirstName(employeeReq.firstName());
        employee.setLastName(employeeReq.lastName());
        employee.setEmail(employeeReq.email());
        employee.setDepartment(employeeReq.department());
        employee.setSalary(employeeReq.salary());

        employee = employeeRepository.save(employee);
        log.info(EMPLOYEE_UPDATE_SUCCESS, id);
        return employeeMapper.toEmployeeRes(employee);
    }

    public void deleteEmployee(UUID id) {
        log.info(EMPLOYEE_DELETE_START, id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(EMPLOYEE_NOT_FOUND, id);
                    return new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_WITH_ID + id);
                });

        employeeRepository.delete(employee);
        log.info(EMPLOYEE_DELETE_SUCCESS, id);
    }

    public List<EmployeeRes> listAllEmployees() {
        log.info(LogMessages.FETCH_ALL_EMPLOYEES_START);
        List<EmployeeRes> employees = employeeRepository.findAll().stream()
                .map(employeeMapper::toEmployeeRes)
                .toList();
        log.info(LogMessages.FETCH_ALL_EMPLOYEES_SUCCESS, employees.size());
        return employees;
    }
}
