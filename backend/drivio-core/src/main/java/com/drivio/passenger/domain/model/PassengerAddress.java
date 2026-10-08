package com.drivio.passenger.domain.model;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record PassengerAddress(
        UUID id,
        UUID passengerId,
        String label,
        String street,
        String city,
        String state,
        String postalCode,
        Instant createdAt
) {

    private static final Set<String> BRAZILIAN_STATES = Set.of(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO",
            "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI",
            "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"
    );

    public PassengerAddress {
        Objects.requireNonNull(id, "Address id is required");
        Objects.requireNonNull(passengerId, "Passenger id is required");
        label = requireText(label, "Address label", 50);
        street = requireText(street, "Street", 160);
        city = requireText(city, "City", 100);
        state = normalizeState(state);
        postalCode = normalizePostalCode(postalCode);
        Objects.requireNonNull(createdAt, "Creation time is required");
    }

    public static PassengerAddress create(
            UUID passengerId,
            String label,
            String street,
            String city,
            String state,
            String postalCode
    ) {
        return new PassengerAddress(
                UUID.randomUUID(),
                passengerId,
                label,
                street,
                city,
                state,
                postalCode,
                Instant.now()
        );
    }

    private static String requireText(String value, String field, int maxLength) {
        Objects.requireNonNull(value, field + " is required");
        String normalized = value.trim();
        if (normalized.isEmpty() || normalized.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
        return normalized;
    }

    private static String normalizeState(String state) {
        Objects.requireNonNull(state, "State is required");
        String normalized = state.trim().toUpperCase(Locale.ROOT);
        if (!BRAZILIAN_STATES.contains(normalized)) {
            throw new IllegalArgumentException("State must be a Brazilian UF code");
        }
        return normalized;
    }

    private static String normalizePostalCode(String postalCode) {
        Objects.requireNonNull(postalCode, "Postal code is required");
        String normalized = postalCode.trim();
        if (!normalized.matches("(?:[0-9]{8}|[0-9]{5}-[0-9]{3})")) {
            throw new IllegalArgumentException("Postal code must be a valid CEP");
        }
        return normalized.replace("-", "");
    }
}
