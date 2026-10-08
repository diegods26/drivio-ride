package com.drivio.driver.domain.model;

import java.time.Year;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public record DriverVehicle(
        UUID id,
        UUID driverId,
        String plate,
        String make,
        String model,
        int year,
        VehicleCategory category,
        boolean isActive,
        UUID approvedBy,
        Instant approvedAt
) {

    public DriverVehicle {
        Objects.requireNonNull(id, "Vehicle id is required");
        Objects.requireNonNull(driverId, "Driver id is required");
        plate = normalizePlate(plate);
        make = requireText(make, "Vehicle make", 80);
        model = requireText(model, "Vehicle model", 80);
        Objects.requireNonNull(category, "Vehicle category is required");
        if (year < 1980 || year > Year.now().getValue() + 1) {
            throw new IllegalArgumentException("Vehicle year is invalid");
        }
        if (isActive && (approvedBy == null || approvedAt == null)) {
            throw new IllegalArgumentException("Active vehicle requires approval audit metadata");
        }
    }

    public static DriverVehicle create(
            UUID driverId,
            String plate,
            String make,
            String model,
            int year,
            VehicleCategory category
    ) {
        return new DriverVehicle(
            UUID.randomUUID(), driverId, plate, make, model, year, category, false, null, null
        );
    }

        public DriverVehicle activate(UUID administratorId) {
        Objects.requireNonNull(administratorId, "Approving administrator is required");
        return new DriverVehicle(
            id, driverId, plate, make, model, year, category, true,
            administratorId, Instant.now()
        );
    }

    public DriverVehicle deactivate() {
        return new DriverVehicle(
            id, driverId, plate, make, model, year, category, false,
            approvedBy, approvedAt
        );
    }

    @Override
    public String toString() {
        return "DriverVehicle[id=" + id + ", driverId=" + driverId
            + ", plate=REDACTED, category=" + category + ", active=" + isActive
            + ", approvedBy=" + approvedBy + "]";
    }

    private static String normalizePlate(String value) {
        Objects.requireNonNull(value, "Vehicle plate is required");
        String normalized = value.replace("-", "").trim().toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z]{3}[0-9][A-Z0-9][0-9]{2}")) {
            throw new IllegalArgumentException("Vehicle plate format is invalid");
        }
        return normalized;
    }

    private static String requireText(String value, String field, int maxLength) {
        Objects.requireNonNull(value, field + " is required");
        String normalized = value.trim();
        if (normalized.isEmpty() || normalized.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
        return normalized;
    }
}