package com.drivio.driver.application.dto;

import com.drivio.driver.domain.model.VehicleCategory;

import java.util.UUID;

public record DriverVehicleView(
        UUID id,
        String plate,
        String make,
        String model,
        int year,
        VehicleCategory category,
        boolean active
) {
}