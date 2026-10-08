package com.drivio.driver.application;

import com.drivio.driver.application.port.out.CurrentAdministratorProvider;
import com.drivio.driver.application.port.out.DriverRepository;
import com.drivio.driver.application.usecase.ApproveDriverVehicleService;
import com.drivio.driver.domain.model.DriverVehicle;
import com.drivio.driver.domain.model.VehicleCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ApproveDriverVehicleServiceTest {

    private final UUID administratorId = UUID.randomUUID();
    private final UUID driverId = UUID.randomUUID();
    private final UUID vehicleId = UUID.randomUUID();
    private CurrentAdministratorProvider administratorProvider;
    private DriverRepository driverRepository;
    private ApproveDriverVehicleService service;

    @BeforeEach
    void setUp() {
        administratorProvider = mock(CurrentAdministratorProvider.class);
        driverRepository = mock(DriverRepository.class);
        service = new ApproveDriverVehicleService(administratorProvider, driverRepository);
        when(administratorProvider.getCurrentAdministratorId()).thenReturn(administratorId);
        when(driverRepository.findVehicleById(vehicleId)).thenReturn(Optional.of(
                new DriverVehicle(
                        vehicleId,
                        driverId,
                        "ABC1D23",
                        "Toyota",
                        "Corolla",
                        LocalDate.now().getYear(),
                        VehicleCategory.STANDARD,
                        false,
                        null,
                        null
                )
        ));
        when(driverRepository.saveVehicle(any(DriverVehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldActivateVehicleAndRecordApprovingAdministrator() {
        var vehicle = service.approve(vehicleId);

        assertThat(vehicle.active()).isTrue();
        verify(administratorProvider).getCurrentAdministratorId();
        verify(driverRepository).saveVehicle(argThat(saved ->
                saved.isActive() && saved.approvedBy().equals(administratorId)
                        && saved.approvedAt() != null
        ));
    }
}
