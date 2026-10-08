package com.drivio.driver.infrastructure.persistence.jpa.entity;

import com.drivio.driver.domain.model.DriverLicenseStatus;
import com.drivio.driver.domain.model.DriverStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "driver_profiles")
public class DriverProfileJpaEntity {

    @Id
    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DriverStatus status;

    @Column(name = "is_online", nullable = false)
    private boolean online;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "license_status", nullable = false, length = 30)
    private DriverLicenseStatus licenseStatus;

    @Column(name = "license_valid_until")
    private LocalDate licenseValidUntil;

    @Column(name = "verification_provider", length = 64)
    private String verificationProvider;

    @Column(name = "verification_reference", length = 100)
    private String verificationReference;

    @Column(name = "license_verified_at")
    private Instant licenseVerifiedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected DriverProfileJpaEntity() {
    }

    public DriverProfileJpaEntity(
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
            Instant updatedAt
    ) {
        this.driverId = driverId;
        this.status = status;
        this.online = online;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.licenseStatus = licenseStatus;
        this.licenseValidUntil = licenseValidUntil;
        this.verificationProvider = verificationProvider;
        this.verificationReference = verificationReference;
        this.licenseVerifiedAt = licenseVerifiedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getDriverId() { return driverId; }
    public DriverStatus getStatus() { return status; }
    public boolean isOnline() { return online; }
    public UUID getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public DriverLicenseStatus getLicenseStatus() { return licenseStatus; }
    public LocalDate getLicenseValidUntil() { return licenseValidUntil; }
    public String getVerificationProvider() { return verificationProvider; }
    public String getVerificationReference() { return verificationReference; }
    public Instant getLicenseVerifiedAt() { return licenseVerifiedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}