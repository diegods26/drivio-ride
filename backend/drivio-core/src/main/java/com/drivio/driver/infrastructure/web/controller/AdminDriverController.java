package com.drivio.driver.infrastructure.web.controller;

import com.drivio.driver.application.dto.DriverProfileView;
import com.drivio.driver.application.dto.DriverVehicleView;
import com.drivio.driver.application.port.in.ApproveDriverVehicleUseCase;
import com.drivio.driver.application.port.in.ApproveDriverUseCase;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/drivers")
public class AdminDriverController {

    private final ApproveDriverUseCase approveDriverUseCase;
    private final ApproveDriverVehicleUseCase approveDriverVehicleUseCase;

    public AdminDriverController(
            ApproveDriverUseCase approveDriverUseCase,
            ApproveDriverVehicleUseCase approveDriverVehicleUseCase
    ) {
        this.approveDriverUseCase = approveDriverUseCase;
        this.approveDriverVehicleUseCase = approveDriverVehicleUseCase;
    }

    @PostMapping("/{driverId}/approval")
    public DriverProfileView approve(@PathVariable UUID driverId) {
        return approveDriverUseCase.approve(driverId);
    }

    @PostMapping("/vehicles/{vehicleId}/approval")
    public DriverVehicleView approveVehicle(@PathVariable UUID vehicleId) {
        return approveDriverVehicleUseCase.approve(vehicleId);
    }
}