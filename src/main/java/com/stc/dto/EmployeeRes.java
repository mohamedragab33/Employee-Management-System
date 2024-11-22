package com.stc.dto;


import lombok.Builder;

import java.util.UUID;

@Builder
public record EmployeeRes(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String department,
        double salary
) {
}