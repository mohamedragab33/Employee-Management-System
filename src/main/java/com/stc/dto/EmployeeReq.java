package com.stc.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record EmployeeReq(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        @Email(message = "Email must be valid") String email,
        @NotBlank(message = "Department is required") String department,
        @Positive(message = "Salary must be positive") double salary
) {
}