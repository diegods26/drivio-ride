package com.drivio.driver.application.usecase;

import com.drivio.driver.application.dto.DriverProfileView;
import com.drivio.driver.application.dto.ManualDriverLicenseReviewCommand;
import com.drivio.driver.application.port.in.ApproveDriverUseCase;
import com.drivio.driver.application.port.out.CurrentAdministratorProvider;
import com.drivio.driver.application.port.out.DriverRepository;
import com.drivio.driver.domain.model.DriverProfile;
import com.drivio.driver.domain.model.DriverVehicle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ApproveDriverService implements ApproveDriverUseCase {

    private final CurrentAdministratorProvider currentAdministratorProvider;
    private final DriverRepository driverRepository;
        private final Clock clock;

    public ApproveDriverService(
            CurrentAdministratorProvider currentAdministratorProvider,
            DriverRepository driverRepository,
            Clock clock
    ) {
        this.currentAdministratorProvider = currentAdministratorProvider;
        this.driverRepository = driverRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public DriverProfileView approve(UUID driverId) {
                UUID administratorId = currentAdministratorProvider.getCurrentAdministratorId();
        DriverProfile profile = driverRepository.findProfile(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver onboarding profile not found"));
        boolean hasActiveVehicle = driverRepository.findVehicles(driverId).stream()
                .anyMatch(DriverVehicle::isActive);
        DriverProfile approved = driverRepository.saveProfile(
                profile.approve(administratorId, hasActiveVehicle, LocalDate.now(clock))
        );
        return new DriverProfileView(
                approved.driverId(),
                approved.status(),
                approved.licenseStatus(),
                approved.licenseValidUntil(),
                approved.online(),
                true,
                approved.licenseReviewReason()
        );
    }

    @Override
    @Transactional
    public DriverProfileView reviewLicense(
            UUID driverId,
            ManualDriverLicenseReviewCommand command
    ) {
        UUID administratorId = currentAdministratorProvider.getCurrentAdministratorId();
        DriverProfile profile = driverRepository.findProfile(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver onboarding profile not found"));
        DriverProfile reviewed = profile.recordManualLicenseReview(
                administratorId,
                command.valid(),
                command.validUntil(),
                command.reason(),
                LocalDate.now(clock)
        );
        DriverProfile saved = driverRepository.saveProfile(reviewed);
        return new DriverProfileView(
                saved.driverId(),
                saved.status(),
                saved.licenseStatus(),
                saved.licenseValidUntil(),
                saved.online(),
                saved.status() == com.drivio.driver.domain.model.DriverStatus.APPROVED,
                saved.licenseReviewReason()
        );
    }
}