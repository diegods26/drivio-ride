package com.drivio.driver.domain.model;

import java.time.LocalDate;
import java.util.Objects;

public record LicenseVerificationResult(
        DriverLicenseStatus status,
        LocalDate validUntil,
        String providerCode,
        String auditReference
) {

    public LicenseVerificationResult {
        Objects.requireNonNull(status, "Verification status is required");
        if (providerCode == null || !providerCode.matches("[A-Z0-9_]{2,64}")) {
            throw new IllegalArgumentException("Provider code is invalid");
        }
        if (auditReference != null
                && !auditReference.matches("[A-Za-z0-9._:-]{1,100}")) {
            throw new IllegalArgumentException("Verification reference is invalid");
        }
        if (status == DriverLicenseStatus.VERIFIED
                && (validUntil == null || auditReference == null)) {
            throw new IllegalArgumentException(
                    "Verified license requires validity and audit reference"
            );
        }
    }

    public static LicenseVerificationResult valid(
            LocalDate validUntil,
            String auditReference
    ) {
        return new LicenseVerificationResult(
                DriverLicenseStatus.VERIFIED, validUntil, "AUTHORIZED_PROVIDER", auditReference
        );
    }

    public static LicenseVerificationResult unavailable(String providerCode) {
        return new LicenseVerificationResult(
                DriverLicenseStatus.UNAVAILABLE, null, providerCode, null
        );
    }

    public static LicenseVerificationResult inconclusive(String providerCode) {
        return new LicenseVerificationResult(
                DriverLicenseStatus.INCONCLUSIVE, null, providerCode, null
        );
    }

    public static LicenseVerificationResult invalid(String providerCode) {
        return new LicenseVerificationResult(
                DriverLicenseStatus.INVALID, null, providerCode, null
        );
    }

    public static LicenseVerificationResult expired(String providerCode) {
        return new LicenseVerificationResult(
                DriverLicenseStatus.EXPIRED, null, providerCode, null
        );
    }
}