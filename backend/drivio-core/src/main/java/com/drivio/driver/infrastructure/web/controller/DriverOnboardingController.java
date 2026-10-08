package com.drivio.driver.infrastructure.web.controller;

import com.drivio.driver.application.dto.DriverAvailabilityCommand;
import com.drivio.driver.application.dto.DriverProfileView;
import com.drivio.driver.application.dto.DriverVehicleView;
import com.drivio.driver.application.dto.RegisterDriverVehicleCommand;
import com.drivio.driver.application.dto.VerifyDriverLicenseCommand;
import com.drivio.driver.application.port.in.ManageDriverOnboardingUseCase;
import com.drivio.driver.domain.valueobject.CnhNumber;
import com.drivio.driver.infrastructure.web.dto.DriverAvailabilityRequest;
import com.drivio.driver.infrastructure.web.dto.RegisterDriverVehicleRequest;
import com.drivio.driver.infrastructure.web.dto.VerifyDriverLicenseRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers/me")
public class DriverOnboardingController {

    private final ManageDriverOnboardingUseCase manageDriverOnboardingUseCase;

    public DriverOnboardingController(
            ManageDriverOnboardingUseCase manageDriverOnboardingUseCase
    ) {
        this.manageDriverOnboardingUseCase = manageDriverOnboardingUseCase;
    }

    @GetMapping
    public DriverProfileView getMyProfile() {
        return manageDriverOnboardingUseCase.getMyProfile();
    }

    @PostMapping("/license-verification")
    public DriverProfileView verifyMyLicense(
            @Valid @RequestBody VerifyDriverLicenseRequest request
    ) {
        return manageDriverOnboardingUseCase.verifyMyLicense(
                new VerifyDriverLicenseCommand(new CnhNumber(request.licenseNumber()))
        );
    }

    @GetMapping("/vehicles")
    public List<DriverVehicleView> listMyVehicles() {
        return manageDriverOnboardingUseCase.listMyVehicles();
    }

    @PostMapping("/vehicles")
    public ResponseEntity<DriverVehicleView> registerMyVehicle(
            @Valid @RequestBody RegisterDriverVehicleRequest request
    ) {
        DriverVehicleView vehicle = manageDriverOnboardingUseCase.registerMyVehicle(
                new RegisterDriverVehicleCommand(
                        request.plate(),
                        request.make(),
                        request.model(),
                        request.year(),
                        request.category()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicle);
    }

    @PutMapping("/availability")
    public DriverProfileView changeAvailability(
            @Valid @RequestBody DriverAvailabilityRequest request
    ) {
        return manageDriverOnboardingUseCase.changeAvailability(
                new DriverAvailabilityCommand(request.online())
        );
    }
}