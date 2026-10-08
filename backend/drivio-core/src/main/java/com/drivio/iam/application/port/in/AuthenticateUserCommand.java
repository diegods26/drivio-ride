package com.drivio.iam.application.port.in;

public record AuthenticateUserCommand(
        String email,
        String password
) {
}
