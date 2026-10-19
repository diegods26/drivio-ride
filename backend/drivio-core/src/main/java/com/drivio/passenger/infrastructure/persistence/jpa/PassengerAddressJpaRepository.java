package com.drivio.passenger.infrastructure.persistence.jpa;

import com.drivio.passenger.infrastructure.persistence.jpa.entity.PassengerAddressJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PassengerAddressJpaRepository
        extends JpaRepository<PassengerAddressJpaEntity, UUID> {

    List<PassengerAddressJpaEntity> findByPassengerIdOrderByCreatedAtDesc(UUID passengerId);

    long deleteByPassengerIdAndId(UUID passengerId, UUID id);
}