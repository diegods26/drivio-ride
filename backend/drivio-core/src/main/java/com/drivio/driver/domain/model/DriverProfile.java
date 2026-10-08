package com.drivio.driver.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record DriverProfile(
        UUID driverId,
        DriverStatus status,
        boolean online,
        UUID approvedBy,
        Instant approvedAt,
        DriverLicenseStatus licenseStatus,
        LocalDate licenseValidUntil,
        String verificationProvider,
        String verificationReference,
        Instant licenseVerifiedAt,
        Instant createdAt,
        Instant updatedAt,
        UUID licenseReviewedBy,
        Instant licenseReviewedAt,
        String licenseReviewReason
) {

    public DriverProfile {
        Objects.requireNonNull(driverId, "Driver id is required");
        Objects.requireNonNull(status, "Driver status is required");
        Objects.requireNonNull(licenseStatus, "License status is required");
        Objects.requireNonNull(createdAt, "Creation time is required");
        Objects.requireNonNull(updatedAt, "Update time is required");
        if (licenseStatus == DriverLicenseStatus.VERIFIED
                && (licenseValidUntil == null || verificationProvider == null
                || verificationReference == null || licenseVerifiedAt == null)) {
            throw new IllegalArgumentException("Verified license metadata is incomplete");
        }
        if (status == DriverStatus.APPROVED
                && licenseStatus != DriverLicenseStatus.VERIFIED) {
            throw new IllegalArgumentException("Driver cannot be approved without a verified license");
        }
        if ((status == DriverStatus.APPROVED || status == DriverStatus.SUSPENDED)
                && (approvedBy == null || approvedAt == null)) {
            throw new IllegalArgumentException("Driver approval audit metadata is required");
        }
        if (status == DriverStatus.REJECTED
                && (licenseReviewedBy == null || licenseReviewedAt == null
                || licenseReviewReason == null)) {
            throw new IllegalArgumentException("License rejection audit metadata is required");
        }
    }

    public static DriverProfile create(UUID driverId) {
        Instant now = Instant.now();
        return new DriverProfile(
                driverId,
                DriverStatus.ONBOARDING,
                false,
                null,
                null,
                DriverLicenseStatus.NOT_SUBMITTED,
                null,
                null,
                null,
                null,
                now,
                now,
                null,
                null,
                null
        );
    }

    public DriverProfile recordLicenseVerification(LicenseVerificationResult result) {
        Objects.requireNonNull(result, "Verification result is required");
        if (status == DriverStatus.SUSPENDED || status == DriverStatus.REJECTED) {
            throw new IllegalStateException("Suspended or rejected driver cannot restart onboarding");
        }
        Instant now = Instant.now();
        DriverLicenseStatus nextLicenseStatus = result.status();
        if (nextLicenseStatus == DriverLicenseStatus.VERIFIED
            && result.validUntil().isBefore(LocalDate.now())) {
            nextLicenseStatus = DriverLicenseStatus.EXPIRED;
        }
        boolean verified = nextLicenseStatus == DriverLicenseStatus.VERIFIED;
        DriverStatus nextStatus = verified ? DriverStatus.PENDING_APPROVAL : DriverStatus.ONBOARDING;
        return new DriverProfile(
                driverId,
                nextStatus,
                false,
                null,
                null,
            nextLicenseStatus,
                verified ? result.validUntil() : null,
                result.providerCode(),
                verified ? result.auditReference() : null,
                verified ? now : null,
                createdAt,
                now,
                null,
                null,
                null
        );
    }

    public DriverProfile recordManualLicenseReview(
            UUID administratorId,
            boolean licenseIsValid,
            LocalDate validUntil,
            String reason,
            LocalDate today
    ) {
        Objects.requireNonNull(administratorId, "Reviewing administrator is required");
        Objects.requireNonNull(today, "Review date is required");
        String normalizedReason = normalizeReviewReason(reason);
        if (licenseIsValid && (validUntil == null || validUntil.isBefore(today))) {
            throw new IllegalArgumentException("A valid license requires a future expiration date");
        }
        if (status == DriverStatus.APPROVED || status == DriverStatus.SUSPENDED) {
            throw new IllegalStateException("Approved or suspended driver cannot be manually reviewed");
        }

        Instant now = Instant.now();
        return new DriverProfile(
                driverId,
                licenseIsValid ? DriverStatus.PENDING_APPROVAL : DriverStatus.REJECTED,
                false,
                null,
                null,
                licenseIsValid ? DriverLicenseStatus.VERIFIED : DriverLicenseStatus.INVALID,
                licenseIsValid ? validUntil : null,
                "MANUAL_ADMIN_REVIEW",
                UUID.randomUUID().toString(),
                licenseIsValid ? now : null,
                createdAt,
                now,
                administratorId,
                now,
                normalizedReason
        );
    }

    public DriverProfile approve(
            UUID administratorId,
            boolean hasActiveVehicle,
            LocalDate today
    ) {
        Objects.requireNonNull(administratorId, "Approving administrator is required");
        Objects.requireNonNull(today, "Approval date is required");
        if (status != DriverStatus.PENDING_APPROVAL
                || licenseStatus != DriverLicenseStatus.VERIFIED
                || licenseValidUntil.isBefore(today)
                || !hasActiveVehicle) {
            throw new IllegalStateException(
                    "Driver requires a current verified license and active vehicle before approval"
            );
        }
        return new DriverProfile(
                driverId,
                DriverStatus.APPROVED,
                false,
                administratorId,
                Instant.now(),
                licenseStatus,
                licenseValidUntil,
                verificationProvider,
                verificationReference,
                licenseVerifiedAt,
                createdAt,
                Instant.now(),
                licenseReviewedBy,
                licenseReviewedAt,
                licenseReviewReason
        );
    }

    public DriverProfile suspend() {
        if (status != DriverStatus.APPROVED) {
            throw new IllegalStateException("Only an approved driver can be suspended");
        }
        return new DriverProfile(
                driverId,
                DriverStatus.SUSPENDED,
                false,
                approvedBy,
                approvedAt,
                licenseStatus,
                licenseValidUntil,
                verificationProvider,
                verificationReference,
                licenseVerifiedAt,
                createdAt,
                Instant.now(),
                licenseReviewedBy,
                licenseReviewedAt,
                licenseReviewReason
        );
    }

    public boolean canGoOnline(boolean hasActiveVehicle, LocalDate today) {
        return status == DriverStatus.APPROVED
                && licenseStatus == DriverLicenseStatus.VERIFIED
                && licenseValidUntil != null
                && !licenseValidUntil.isBefore(today)
                && hasActiveVehicle;
    }

    public DriverProfile setOnline(boolean online, boolean hasActiveVehicle, LocalDate today) {
        if (online && !canGoOnline(hasActiveVehicle, today)) {
            throw new IllegalStateException(
                    "Driver must be approved with a current license and active vehicle to go online"
            );
        }
        return new DriverProfile(
                driverId,
                status,
                online,
                approvedBy,
                approvedAt,
                licenseStatus,
                licenseValidUntil,
                verificationProvider,
                verificationReference,
                licenseVerifiedAt,
                createdAt,
                Instant.now(),
                licenseReviewedBy,
                licenseReviewedAt,
                licenseReviewReason
        );
    }

    private static String normalizeReviewReason(String reason) {
        Objects.requireNonNull(reason, "Review reason is required");
        String normalized = reason.trim();
        if (normalized.isEmpty() || normalized.length() > 500) {
            throw new IllegalArgumentException("Review reason must contain 1 to 500 characters");
        }
        if (normalized.matches("(?s).*\\d{11,}.*")) {
            throw new IllegalArgumentException("Review reason must not contain document numbers");
        }
        return normalized;
    }

    @Override
    public String toString() {
        return "DriverProfile[driverId=" + driverId
                + ", status=" + status
                + ", online=" + online
                + ", licenseStatus=" + licenseStatus
                + ", licenseValidUntil=" + licenseValidUntil
                + ", licenseReviewReason=REDACTED]";
    }
}