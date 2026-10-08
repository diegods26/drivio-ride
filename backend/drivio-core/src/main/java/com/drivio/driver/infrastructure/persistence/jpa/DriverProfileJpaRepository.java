package com.drivio.driver.infrastructure.persistence.jpa;

import com.drivio.driver.infrastructure.persistence.jpa.entity.DriverProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DriverProfileJpaRepository extends JpaRepository<DriverProfileJpaEntity, UUID> {
}