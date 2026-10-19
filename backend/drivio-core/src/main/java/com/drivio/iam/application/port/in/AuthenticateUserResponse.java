package com.drivio.iam.application.port.in;

import java.util.UUID;

public record AuthenticateUserResponse(
        UUID userId,
        String email,
        String role
) {
}
