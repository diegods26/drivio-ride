package com.drivio.driver.infrastructure.persistence.jpa;

import com.drivio.driver.infrastructure.persistence.jpa.entity.DriverVehicleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DriverVehicleJpaRepository extends JpaRepository<DriverVehicleJpaEntity, UUID> {

    List<DriverVehicleJpaEntity> findByDriverIdOrderByPlate(UUID driverId);

    Optional<DriverVehicleJpaEntity> findByDriverIdAndId(UUID driverId, UUID id);
}