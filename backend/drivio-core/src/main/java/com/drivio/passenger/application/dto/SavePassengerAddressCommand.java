package com.drivio.passenger.application.dto;

public record SavePassengerAddressCommand(
        String label,
        String street,
        String city,
        String state,
        String postalCode
) {
}