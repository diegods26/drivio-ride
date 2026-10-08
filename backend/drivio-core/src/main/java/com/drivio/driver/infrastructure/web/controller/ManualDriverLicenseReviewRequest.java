package com.drivio.driver.infrastructure.web.controller;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ManualDriverLicenseReviewRequest(
    @NotNull Boolean valid,
        LocalDate validUntil,
        @NotBlank @Size(max = 500) String reason
) {

    @AssertTrue(message = "A valid license requires its expiration date")
    public boolean isValidExpirationPresent() {
        return !Boolean.TRUE.equals(valid) || validUntil != null;
    }

    @Override
    public String toString() {
        return "ManualDriverLicenseReviewRequest[valid=" + valid
                + ", validUntil=" + validUntil + ", reason=REDACTED]";
    }
}