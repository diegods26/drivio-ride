package com.drivio.passenger.application.dto;

import java.util.UUID;

public record PassengerProfileView(
        UUID id,
        String fullName,
        String email,
        String phoneNumber
) {
}