package com.drivio.passenger.infrastructure.persistence;

import com.drivio.iam.domain.model.Role;
import com.drivio.iam.infrastructure.persistence.jpa.UserJpaRepository;
import com.drivio.iam.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.drivio.passenger.application.port.out.PassengerProfileRepository;
import com.drivio.passenger.domain.model.PassengerAddress;
import com.drivio.passenger.domain.model.PassengerProfile;
import com.drivio.passenger.domain.valueobject.PhoneNumber;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PassengerProfileJpaAdapterIntegrationTest {

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private PassengerProfileRepository passengerProfileRepository;

    @Test
    void shouldPersistProfileAndKeepAddressDeletionScopedToOwner() {
        UUID ownerId = createPassenger("owner@example.com", "41134746059");
        UUID otherPassengerId = createPassenger("other@example.com", "52998224725");

        PassengerProfile profile = PassengerProfile.create(
                ownerId,
                new PhoneNumber("+5511999999999")
        );
        passengerProfileRepository.saveProfile(profile);

        PassengerAddress address = PassengerAddress.create(
                ownerId,
                "Casa",
                "Rua das Flores, 10",
                "Campinas",
                "SP",
                "13010-000"
        );
        passengerProfileRepository.saveAddress(address);

        assertThat(passengerProfileRepository.findProfileByPassengerId(ownerId))
                .get()
                .extracting(saved -> saved.phoneNumber().value())
                .isEqualTo("+5511999999999");
        assertThat(passengerProfileRepository.findAddressesByPassengerId(ownerId))
                .containsExactly(address);
        assertThat(passengerProfileRepository.deleteAddress(otherPassengerId, address.id()))
                .isFalse();
        assertThat(passengerProfileRepository.deleteAddress(ownerId, address.id()))
                .isTrue();
        assertThat(passengerProfileRepository.findAddressesByPassengerId(ownerId))
                .isEmpty();
    }

        private UUID createPassenger(String email, String cpf) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        userRepository.save(new UserJpaEntity(
                id,
                "Test Passenger",
                email,
                cpf,
                "encoded-password",
                Role.PASSENGER,
                true,
                now,
                now
        ));
        return id;
    }
}
