package com.drivio.passenger.application.usecase;

import com.drivio.passenger.application.dto.PassengerAddressView;
import com.drivio.passenger.application.dto.PassengerIdentity;
import com.drivio.passenger.application.dto.PassengerProfileView;
import com.drivio.passenger.application.dto.SavePassengerAddressCommand;
import com.drivio.passenger.application.dto.UpdatePassengerProfileCommand;
import com.drivio.passenger.application.port.in.ManagePassengerProfileUseCase;
import com.drivio.passenger.application.port.out.CurrentPassengerProvider;
import com.drivio.passenger.application.port.out.PassengerProfileRepository;
import com.drivio.passenger.domain.model.PassengerAddress;
import com.drivio.passenger.domain.model.PassengerProfile;
import com.drivio.passenger.domain.valueobject.PhoneNumber;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ManagePassengerProfileService implements ManagePassengerProfileUseCase {

    private final CurrentPassengerProvider currentPassengerProvider;
    private final PassengerProfileRepository passengerProfileRepository;

    public ManagePassengerProfileService(
            CurrentPassengerProvider currentPassengerProvider,
            PassengerProfileRepository passengerProfileRepository
    ) {
        this.currentPassengerProvider = currentPassengerProvider;
        this.passengerProfileRepository = passengerProfileRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerProfileView getMyProfile() {
        PassengerIdentity passenger = currentPassengerProvider.getCurrentPassenger();
        String phoneNumber = passengerProfileRepository
                .findProfileByPassengerId(passenger.id())
                .map(PassengerProfile::phoneNumber)
                .map(PhoneNumber::value)
                .orElse(null);

        return new PassengerProfileView(
                passenger.id(), passenger.fullName(), passenger.email(), phoneNumber
        );
    }

    @Override
    @Transactional
    public PassengerProfileView updateMyProfile(UpdatePassengerProfileCommand command) {
        PassengerIdentity passenger = currentPassengerProvider.getCurrentPassenger();
        PhoneNumber phoneNumber = new PhoneNumber(command.phoneNumber());
        PassengerProfile profile = passengerProfileRepository
                .findProfileByPassengerId(passenger.id())
                .map(existing -> existing.updatePhoneNumber(phoneNumber))
                .orElseGet(() -> PassengerProfile.create(passenger.id(), phoneNumber));

        passengerProfileRepository.saveProfile(profile);
        return new PassengerProfileView(
                passenger.id(), passenger.fullName(), passenger.email(), phoneNumber.value()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PassengerAddressView> listMyAddresses() {
        UUID passengerId = currentPassengerProvider.getCurrentPassenger().id();
        return passengerProfileRepository.findAddressesByPassengerId(passengerId)
                .stream()
                .map(ManagePassengerProfileService::toView)
                .toList();
    }

    @Override
    @Transactional
    public PassengerAddressView addMyAddress(SavePassengerAddressCommand command) {
        UUID passengerId = currentPassengerProvider.getCurrentPassenger().id();
        PassengerAddress address = PassengerAddress.create(
                passengerId,
                command.label(),
                command.street(),
                command.city(),
                command.state(),
                command.postalCode()
        );
        return toView(passengerProfileRepository.saveAddress(address));
    }

    @Override
    @Transactional
    public boolean removeMyAddress(UUID addressId) {
        UUID passengerId = currentPassengerProvider.getCurrentPassenger().id();
        return passengerProfileRepository.deleteAddress(passengerId, addressId);
    }

    private static PassengerAddressView toView(PassengerAddress address) {
        return new PassengerAddressView(
                address.id(),
                address.label(),
                address.street(),
                address.city(),
                address.state(),
                address.postalCode()
        );
    }
}