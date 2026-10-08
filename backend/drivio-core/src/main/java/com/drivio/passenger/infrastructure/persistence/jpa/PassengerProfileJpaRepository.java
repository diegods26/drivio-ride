package com.drivio.passenger.infrastructure.persistence.jpa;

import com.drivio.passenger.infrastructure.persistence.jpa.entity.PassengerProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PassengerProfileJpaRepository
        extends JpaRepository<PassengerProfileJpaEntity, UUID> {
}