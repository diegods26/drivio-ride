package com.drivio.passenger.application.dto;

import java.util.UUID;

public record PassengerIdentity(UUID id, String fullName, String email) {
}