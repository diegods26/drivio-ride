package com.drivio.driver.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

public record DriverAvailabilityRequest(@NotNull Boolean online) {
}