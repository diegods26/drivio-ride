package com.drivio.passenger.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "passenger_profiles")
public class PassengerProfileJpaEntity {

    @Id
    @Column(name = "passenger_id", nullable = false)
    private UUID passengerId;

    @Column(name = "phone_number", length = 16)
    private String phoneNumber;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PassengerProfileJpaEntity() {
    }

    public PassengerProfileJpaEntity(
            UUID passengerId,
            String phoneNumber,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.passengerId = passengerId;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getPassengerId() {
        return passengerId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}