package com.drivio.driver.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyDriverLicenseRequest(
        @NotBlank(message = "Driver license number is required")
        @Pattern(regexp = "[0-9]{11}", message = "Driver license number format is invalid")
        String licenseNumber
) {

    @Override
    public String toString() {
        return "VerifyDriverLicenseRequest[licenseNumber=REDACTED]";
    }
}