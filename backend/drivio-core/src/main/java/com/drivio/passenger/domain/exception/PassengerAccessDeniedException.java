package com.drivio.passenger.domain.exception;

public final class PassengerAccessDeniedException extends RuntimeException {

    public PassengerAccessDeniedException() {
        super("Passenger access denied");
    }
}