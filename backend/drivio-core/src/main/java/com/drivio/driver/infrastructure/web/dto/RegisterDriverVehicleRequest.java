package com.drivio.driver.infrastructure.web.dto;

import com.drivio.driver.domain.model.VehicleCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterDriverVehicleRequest(
        @NotBlank
        @Pattern(regexp = "(?i)[A-Z]{3}-?[0-9][A-Z0-9][0-9]{2}")
        String plate,
        @NotBlank @Size(max = 80) String make,
        @NotBlank @Size(max = 80) String model,
        @Min(1980) @Max(2100) int year,
        @NotNull VehicleCategory category
) {
}