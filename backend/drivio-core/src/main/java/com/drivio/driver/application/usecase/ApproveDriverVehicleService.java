package com.drivio.driver.application.usecase;

import com.drivio.driver.application.dto.DriverVehicleView;
import com.drivio.driver.application.port.in.ApproveDriverVehicleUseCase;
import com.drivio.driver.application.port.out.CurrentAdministratorProvider;
import com.drivio.driver.application.port.out.DriverRepository;
import com.drivio.driver.domain.model.DriverVehicle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ApproveDriverVehicleService implements ApproveDriverVehicleUseCase {

    private final CurrentAdministratorProvider currentAdministratorProvider;
    private final DriverRepository driverRepository;

    public ApproveDriverVehicleService(
            CurrentAdministratorProvider currentAdministratorProvider,
            DriverRepository driverRepository
    ) {
        this.currentAdministratorProvider = currentAdministratorProvider;
        this.driverRepository = driverRepository;
    }

    @Override
    @Transactional
    public DriverVehicleView approve(UUID vehicleId) {
        UUID administratorId = currentAdministratorProvider.getCurrentAdministratorId();
        DriverVehicle vehicle = driverRepository.findVehicleById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        DriverVehicle activeVehicle = driverRepository.saveVehicle(vehicle.activate(administratorId));
        return new DriverVehicleView(
                activeVehicle.id(),
                activeVehicle.plate(),
                activeVehicle.make(),
                activeVehicle.model(),
                activeVehicle.year(),
                activeVehicle.category(),
                activeVehicle.isActive()
        );
    }
}