package com.stc.controller;

import com.stc.dto.EmployeeReq;
import com.stc.dto.EmployeeRes;
import com.stc.service.EmailValidationService;
import com.stc.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmailValidationService emailValidationService;

    public EmployeeController(EmployeeService employeeService, EmailValidationService emailValidationService) {
        this.employeeService = employeeService;
        this.emailValidationService = emailValidationService;
    }

    @PostMapping
    public ResponseEntity<EmployeeRes> createEmployee(@Valid @RequestBody EmployeeReq employeeReq) {
        EmployeeRes response = employeeService.createEmployee(employeeReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeRes> getEmployeeById(@PathVariable UUID id) {
        EmployeeRes response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeRes> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeReq employeeReq) {
        EmployeeRes response = employeeService.updateEmployee(id, employeeReq);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EmployeeRes>> listAllEmployees() {
        List<EmployeeRes> employees = employeeService.listAllEmployees();
        return ResponseEntity.ok(employees);
    }
    @PostMapping("/validate-email")
    public ResponseEntity<Boolean> validateEmail(@RequestBody String email) {
        boolean isValid = emailValidationService.validateEmail(email);
        return ResponseEntity.ok(isValid);
    }

}
