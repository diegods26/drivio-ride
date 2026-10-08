package com.drivio.driver.domain;

import com.drivio.driver.domain.model.DriverVehicle;
import com.drivio.driver.domain.model.VehicleCategory;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DriverVehicleTest {

    @Test
    void shouldNormalizeAndAcceptMercosulPlate() {
        DriverVehicle vehicle = DriverVehicle.create(
                UUID.randomUUID(), "abc1d23", "Toyota", "Corolla", 2024,
                VehicleCategory.COMFORT
        );

        assertThat(vehicle.plate()).isEqualTo("ABC1D23");
        assertThat(vehicle.isActive()).isFalse();
        DriverVehicle activated = vehicle.activate(UUID.randomUUID());
        assertThat(activated.isActive()).isTrue();
        assertThat(activated.approvedBy()).isNotNull();
        assertThat(activated.approvedAt()).isNotNull();
    }

    @Test
    void shouldAcceptLegacyBrazilianPlate() {
        DriverVehicle vehicle = DriverVehicle.create(
                UUID.randomUUID(), "abc-1234", "Honda", "Civic", 2020,
                VehicleCategory.STANDARD
        );

        assertThat(vehicle.plate()).isEqualTo("ABC1234");
    }

    @Test
    void shouldRejectInvalidPlate() {
        assertThatThrownBy(() -> DriverVehicle.create(
                UUID.randomUUID(), "INVALID", "Honda", "Civic", 2020,
                VehicleCategory.STANDARD
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
