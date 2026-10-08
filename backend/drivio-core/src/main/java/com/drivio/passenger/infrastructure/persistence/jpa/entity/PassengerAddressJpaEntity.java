package com.drivio.passenger.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "passenger_addresses")
public class PassengerAddressJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "passenger_id", nullable = false)
    private UUID passengerId;

    @Column(name = "label", nullable = false, length = 50)
    private String label;

    @Column(name = "street", nullable = false, length = 160)
    private String street;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 2)
    private String state;

    @Column(name = "postal_code", nullable = false, length = 8)
    private String postalCode;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected PassengerAddressJpaEntity() {
    }

    public PassengerAddressJpaEntity(
            UUID id,
            UUID passengerId,
            String label,
            String street,
            String city,
            String state,
            String postalCode,
            Instant createdAt
    ) {
        this.id = id;
        this.passengerId = passengerId;
        this.label = label;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPassengerId() {
        return passengerId;
    }

    public String getLabel() {
        return label;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}