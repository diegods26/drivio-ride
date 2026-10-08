package com.drivio.passenger.domain.model;

import com.drivio.passenger.domain.valueobject.PhoneNumber;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record PassengerProfile(
        UUID passengerId,
        PhoneNumber phoneNumber,
        Instant createdAt,
        Instant updatedAt
) {

    public PassengerProfile {
        Objects.requireNonNull(passengerId, "Passenger id is required");
        Objects.requireNonNull(createdAt, "Creation time is required");
        Objects.requireNonNull(updatedAt, "Update time is required");
    }

    public static PassengerProfile create(UUID passengerId, PhoneNumber phoneNumber) {
        Instant now = Instant.now();
        return new PassengerProfile(passengerId, phoneNumber, now, now);
    }

    public PassengerProfile updatePhoneNumber(PhoneNumber phoneNumber) {
        return new PassengerProfile(passengerId, phoneNumber, createdAt, Instant.now());
    }
}