package com.drivio.passenger.application;

import com.drivio.passenger.application.dto.PassengerIdentity;
import com.drivio.passenger.application.dto.SavePassengerAddressCommand;
import com.drivio.passenger.application.dto.UpdatePassengerProfileCommand;
import com.drivio.passenger.application.port.out.CurrentPassengerProvider;
import com.drivio.passenger.application.port.out.PassengerProfileRepository;
import com.drivio.passenger.application.usecase.ManagePassengerProfileService;
import com.drivio.passenger.domain.model.PassengerAddress;
import com.drivio.passenger.domain.model.PassengerProfile;
import com.drivio.passenger.domain.valueobject.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ManagePassengerProfileServiceTest {

    private final UUID passengerId = UUID.randomUUID();
    private CurrentPassengerProvider currentPassengerProvider;
    private PassengerProfileRepository repository;
    private ManagePassengerProfileService service;

    @BeforeEach
    void setUp() {
        currentPassengerProvider = mock(CurrentPassengerProvider.class);
        repository = mock(PassengerProfileRepository.class);
        service = new ManagePassengerProfileService(currentPassengerProvider, repository);
        when(currentPassengerProvider.getCurrentPassenger()).thenReturn(
                new PassengerIdentity(passengerId, "Diego Santos", "diego@example.com")
        );
    }

    @Test
    void shouldUpdatePhoneForAuthenticatedPassenger() {
        when(repository.findProfileByPassengerId(passengerId)).thenReturn(Optional.empty());

        var profile = service.updateMyProfile(
                new UpdatePassengerProfileCommand("+5511999999999")
        );

        assertThat(profile.id()).isEqualTo(passengerId);
        assertThat(profile.phoneNumber()).isEqualTo("+5511999999999");
        verify(repository).saveProfile(argThat(saved ->
                saved.passengerId().equals(passengerId)
                        && saved.phoneNumber().value().equals("+5511999999999")
        ));
    }

    @Test
    void shouldRejectInvalidPhoneBeforePersistence() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.updateMyProfile(
                        new UpdatePassengerProfileCommand("11999999999")))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(repository);
    }

    @Test
    void shouldBindNewAddressToAuthenticatedPassenger() {
        when(repository.saveAddress(any(PassengerAddress.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var address = service.addMyAddress(new SavePassengerAddressCommand(
                "Casa", "Rua das Flores, 10", "Campinas", "SP", "13010-000"
        ));

        assertThat(address.id()).isNotNull();
        verify(repository).saveAddress(argThat(saved ->
                saved.passengerId().equals(passengerId)
                        && saved.postalCode().equals("13010000")
        ));
    }

    @Test
    void shouldDeleteAddressOnlyForAuthenticatedPassenger() {
        UUID addressId = UUID.randomUUID();
        when(repository.deleteAddress(passengerId, addressId)).thenReturn(true);

        assertThat(service.removeMyAddress(addressId)).isTrue();
        verify(repository).deleteAddress(passengerId, addressId);
    }
}
