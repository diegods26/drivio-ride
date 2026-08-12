package com.drivio.iam.application.dto;

public record RegisterUserCommand(
        String fullName,
        String email,
        String password
) {
}
