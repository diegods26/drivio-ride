package com.drivio.driver.application.dto;

import com.drivio.driver.domain.model.DriverLicenseStatus;
import com.drivio.driver.domain.model.DriverStatus;

import java.time.LocalDate;
import java.util.UUID;

public record DriverProfileView(
        UUID driverId,
        DriverStatus status,
        DriverLicenseStatus licenseStatus,
        LocalDate licenseValidUntil,
        boolean online,
        boolean approved
) {
}