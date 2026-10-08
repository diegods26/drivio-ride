package com.drivio.passenger.application.port.out;

import com.drivio.passenger.application.dto.PassengerIdentity;

public interface CurrentPassengerProvider {

    PassengerIdentity getCurrentPassenger();
}