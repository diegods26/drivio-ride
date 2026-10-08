package com.drivio.passenger.application.dto;

import java.util.UUID;

public record PassengerAddressView(
        UUID id,
        String label,
        String street,
        String city,
        String state,
        String postalCode
) {
}