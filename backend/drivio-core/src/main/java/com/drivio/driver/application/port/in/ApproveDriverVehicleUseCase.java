package com.drivio.driver.application.port.in;

import com.drivio.driver.application.dto.DriverVehicleView;

import java.util.UUID;

public interface ApproveDriverVehicleUseCase {

    DriverVehicleView approve(UUID vehicleId);
}