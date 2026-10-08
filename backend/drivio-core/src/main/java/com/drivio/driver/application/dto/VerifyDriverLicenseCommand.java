package com.drivio.driver.application.dto;

import com.drivio.driver.domain.valueobject.CnhNumber;

public record VerifyDriverLicenseCommand(CnhNumber licenseNumber) {
}