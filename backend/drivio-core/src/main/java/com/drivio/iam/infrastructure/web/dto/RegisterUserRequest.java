package com.drivio.iam.infrastructure.web.dto;

public record RegisterUserRequest(
        String fullname,
        String email,
        String password
) {
}
