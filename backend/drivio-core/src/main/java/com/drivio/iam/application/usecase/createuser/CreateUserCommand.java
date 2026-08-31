package com.drivio.iam.application.usecase.createuser;

public record CreateUserCommand(
        String fullName,
        String email,
        String password
) {
}
