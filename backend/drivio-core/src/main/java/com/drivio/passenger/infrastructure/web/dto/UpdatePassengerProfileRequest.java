package com.drivio.passenger.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePassengerProfileRequest(
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "\\+[1-9][0-9]{7,14}", message = "Phone number must use E.164 format")
        String phoneNumber
) {
}