package com.drivio.driver.application.port.out;

import com.drivio.driver.domain.model.DriverProfile;
import com.drivio.driver.domain.model.DriverVehicle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DriverRepository {

    Optional<DriverProfile> findProfile(UUID driverId);

    DriverProfile saveProfile(DriverProfile profile);

    List<DriverVehicle> findVehicles(UUID driverId);

    Optional<DriverVehicle> findVehicle(UUID driverId, UUID vehicleId);

    Optional<DriverVehicle> findVehicleById(UUID vehicleId);

    DriverVehicle saveVehicle(DriverVehicle vehicle);
}