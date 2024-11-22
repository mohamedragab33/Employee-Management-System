package com.stc.service;

import com.stc.dto.EmployeeReq;
import com.stc.dto.EmployeeRes;
import com.stc.entity.Employee;
import com.stc.exception.EmployeeNotFoundException;
import com.stc.exception.InvalidInputException;
import com.stc.mapper.EmployeeMapper;
import com.stc.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    public static final String EMPLOYEE_NOT_FOUND_WITH_ID = "Employee not found with ID: ";
    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmailValidationService emailValidationService;

    @Mock
    private DepartmentValidationService departmentValidationService;

    @Mock
    private EmailService emailService;

    @Mock
    private EmployeeMapper employeeMapper;

    private EmployeeReq employeeReq;
    private Employee employee;
    private EmployeeRes employeeRes;
    private UUID employeeId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employeeId = UUID.randomUUID();
        employeeReq = new EmployeeReq("Mo", "Elkazzaz", "Mo.Elkazzaz@example.com", "IT", 5000);
        employee = new Employee(employeeId, "Mo", "Elkazzaz", "Mo.Elkazzaz@example.com", "IT", 5000);
        employeeRes = new EmployeeRes("Mo", "Elkazzaz", "Mo.Elkazzaz@example.com", "IT", 5000);
    }

    @Test
    void createEmployee_shouldCreateAndReturnEmployee() {
        when(emailValidationService.validateEmail(employeeReq.email())).thenReturn(true);
        when(departmentValidationService.validateDepartment(employeeReq.department())).thenReturn(true);
        when(employeeMapper.toEntity(employeeReq)).thenReturn(employee);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toEmployeeRes(employee)).thenReturn(employeeRes);

        EmployeeRes result = employeeService.createEmployee(employeeReq);

        assertNotNull(result);
        assertEquals(employeeRes, result);
        verify(emailValidationService).validateEmail(employeeReq.email());
        verify(departmentValidationService).validateDepartment(employeeReq.department());
        verify(employeeRepository).save(any(Employee.class));
        verify(emailService).sendEmployeeCreationNotification(employee);
    }

    @Test
    void createEmployee_shouldThrowInvalidInputExceptionForInvalidEmail() {
        when(emailValidationService.validateEmail(employeeReq.email())).thenReturn(false);

        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> employeeService.createEmployee(employeeReq));

        assertEquals("Invalid email address", exception.getMessage());
        verify(emailValidationService).validateEmail(employeeReq.email());
    }

    @Test
    void createEmployee_shouldThrowInvalidInputExceptionForInvalidDepartment() {
        when(emailValidationService.validateEmail(employeeReq.email())).thenReturn(true);
        when(departmentValidationService.validateDepartment(employeeReq.department())).thenReturn(false);

        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> employeeService.createEmployee(employeeReq));

        assertEquals("Invalid department", exception.getMessage());
        verify(emailValidationService).validateEmail(employeeReq.email());
        verify(departmentValidationService).validateDepartment(employeeReq.department());
    }

    @Test
    void getEmployeeById_shouldReturnEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeMapper.toEmployeeRes(employee)).thenReturn(employeeRes);

        EmployeeRes result = employeeService.getEmployeeById(employeeId);

        assertNotNull(result);
        assertEquals(employeeRes, result);
        verify(employeeRepository).findById(employeeId);
        verify(employeeMapper).toEmployeeRes(employee);
    }

    @Test
    void getEmployeeById_shouldThrowEmployeeNotFoundException() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(employeeId));

        assertEquals(EMPLOYEE_NOT_FOUND_WITH_ID + employeeId, exception.getMessage());
        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void updateEmployee_shouldUpdateAndReturnEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toEmployeeRes(employee)).thenReturn(employeeRes);

        EmployeeRes result = employeeService.updateEmployee(employeeId, employeeReq);

        assertNotNull(result);
        assertEquals(employeeRes, result);
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(employee);
        verify(employeeMapper).toEmployeeRes(employee);
    }

    @Test
    void updateEmployee_shouldThrowEmployeeNotFoundException() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.updateEmployee(employeeId, employeeReq));

        assertEquals(EMPLOYEE_NOT_FOUND_WITH_ID + employeeId, exception.getMessage());
        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void deleteEmployee_shouldDeleteEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        employeeService.deleteEmployee(employeeId);

        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).delete(employee);
    }

    @Test
    void deleteEmployee_shouldThrowEmployeeNotFoundException() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployee(employeeId));

        assertEquals(EMPLOYEE_NOT_FOUND_WITH_ID + employeeId, exception.getMessage());
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    void listAllEmployees_shouldReturnListOfEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(employeeMapper.toEmployeeRes(employee)).thenReturn(employeeRes);

        List<EmployeeRes> result = employeeService.listAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(employeeRes, result.get(0));
        verify(employeeRepository).findAll();
        verify(employeeMapper).toEmployeeRes(employee);
    }
}
