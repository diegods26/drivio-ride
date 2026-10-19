package com.drivio.passenger.application.port.out;

import com.drivio.passenger.domain.model.PassengerAddress;
import com.drivio.passenger.domain.model.PassengerProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PassengerProfileRepository {

    Optional<PassengerProfile> findProfileByPassengerId(UUID passengerId);

    PassengerProfile saveProfile(PassengerProfile profile);

    List<PassengerAddress> findAddressesByPassengerId(UUID passengerId);

    PassengerAddress saveAddress(PassengerAddress address);

    boolean deleteAddress(UUID passengerId, UUID addressId);
}