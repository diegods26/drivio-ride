package com.drivio.driver.application.dto;

import java.time.LocalDate;

public record ManualDriverLicenseReviewCommand(
        boolean valid,
        LocalDate validUntil,
        String reason
) {

        @Override
        public String toString() {
                return "ManualDriverLicenseReviewCommand[valid=" + valid
                                + ", validUntil=" + validUntil + ", reason=REDACTED]";
        }
}