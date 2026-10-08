package com.drivio.driver.infrastructure.persistence.jpa.entity;

import com.drivio.driver.domain.model.VehicleCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;
import java.time.Instant;

@Entity
@Table(name = "driver_vehicles")
public class DriverVehicleJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Column(name = "plate", nullable = false, length = 7)
    private String plate;

    @Column(name = "make", nullable = false, length = 80)
    private String make;

    @Column(name = "model", nullable = false, length = 80)
    private String model;

    @Column(name = "model_year", nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private VehicleCategory category;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    protected DriverVehicleJpaEntity() {
    }

    public DriverVehicleJpaEntity(
            UUID id,
            UUID driverId,
            String plate,
            String make,
            String model,
            int year,
            VehicleCategory category,
            boolean active,
            UUID approvedBy,
            Instant approvedAt
    ) {
        this.id = id;
        this.driverId = driverId;
        this.plate = plate;
        this.make = make;
        this.model = model;
        this.year = year;
        this.category = category;
        this.active = active;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
    }

    public UUID getId() { return id; }
    public UUID getDriverId() { return driverId; }
    public String getPlate() { return plate; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public VehicleCategory getCategory() { return category; }
    public boolean isActive() { return active; }
    public UUID getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
}