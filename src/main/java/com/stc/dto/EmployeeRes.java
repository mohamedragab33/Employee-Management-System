package com.stc.dto;


import lombok.Builder;

@Builder
public record EmployeeRes(
        String firstName,
        String lastName,
        String email,
        String department,
        double salary
) {
}