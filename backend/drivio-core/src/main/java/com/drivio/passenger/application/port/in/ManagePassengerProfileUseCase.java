package com.drivio.passenger.application.port.in;

import com.drivio.passenger.application.dto.PassengerAddressView;
import com.drivio.passenger.application.dto.PassengerProfileView;
import com.drivio.passenger.application.dto.SavePassengerAddressCommand;
import com.drivio.passenger.application.dto.UpdatePassengerProfileCommand;

import java.util.List;
import java.util.UUID;

public interface ManagePassengerProfileUseCase {

    PassengerProfileView getMyProfile();

    PassengerProfileView updateMyProfile(UpdatePassengerProfileCommand command);

    List<PassengerAddressView> listMyAddresses();

    PassengerAddressView addMyAddress(SavePassengerAddressCommand command);

    boolean removeMyAddress(UUID addressId);
}