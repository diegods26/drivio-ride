package com.drivio.passenger.domain;

import com.drivio.passenger.domain.model.PassengerAddress;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PassengerAddressTest {

    @Test
    void shouldCreateBrazilianAddressAndNormalizeCep() {
        UUID passengerId = UUID.randomUUID();

        PassengerAddress address = PassengerAddress.create(
                passengerId,
                "Casa",
                "Rua das Flores, 10",
                "Campinas",
                "sp",
                "13010-000"
        );

        assertThat(address.passengerId()).isEqualTo(passengerId);
        assertThat(address.state()).isEqualTo("SP");
        assertThat(address.postalCode()).isEqualTo("13010000");
    }

    @Test
    void shouldRejectMalformedCep() {
        assertThatThrownBy(() -> PassengerAddress.create(
                UUID.randomUUID(),
                "Casa",
                "Rua das Flores, 10",
                "Campinas",
                "SP",
                "13ABC-000"
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectInvalidStateCode() {
        assertThatThrownBy(() -> PassengerAddress.create(
                UUID.randomUUID(),
                "Casa",
                "Rua das Flores, 10",
                "Campinas",
                "Sao Paulo",
                "13010000"
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectUnknownTwoLetterStateCode() {
        assertThatThrownBy(() -> PassengerAddress.create(
                UUID.randomUUID(),
                "Casa",
                "Rua das Flores, 10",
                "Campinas",
                "ZZ",
                "13010000"
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
