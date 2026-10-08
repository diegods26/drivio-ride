package com.drivio.driver.application.dto;

import com.drivio.driver.domain.model.VehicleCategory;

public record RegisterDriverVehicleCommand(
        String plate,
        String make,
        String model,
        int year,
        VehicleCategory category
) {
}