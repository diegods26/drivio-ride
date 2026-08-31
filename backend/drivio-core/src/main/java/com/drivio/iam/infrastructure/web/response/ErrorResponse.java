package com.drivio.iam.infrastructure.web.response;

import java.time.Instant;

public record ErrorResponse(
        int status,
        String code,
        String message,
        Instant timestamp
) {
}
