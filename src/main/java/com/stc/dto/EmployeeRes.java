package com.stc.dto;


import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EmployeeRes(
        String firstName,
        String lastName,
        String email,
        String department,
        double salary,
        LocalDateTime createdAt
) {
}