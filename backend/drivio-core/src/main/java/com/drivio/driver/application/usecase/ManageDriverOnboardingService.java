package com.drivio.driver.application.usecase;

import com.drivio.driver.application.dto.CurrentDriver;
import com.drivio.driver.application.dto.DriverAvailabilityCommand;
import com.drivio.driver.application.dto.DriverProfileView;
import com.drivio.driver.application.dto.DriverVehicleView;
import com.drivio.driver.application.dto.RegisterDriverVehicleCommand;
import com.drivio.driver.application.dto.VerifyDriverLicenseCommand;
import com.drivio.driver.application.port.in.ManageDriverOnboardingUseCase;
import com.drivio.driver.application.port.out.CurrentDriverProvider;
import com.drivio.driver.application.port.out.DriverLicenseVerifier;
import com.drivio.driver.application.port.out.DriverRepository;
import com.drivio.driver.domain.model.DriverProfile;
import com.drivio.driver.domain.model.DriverVehicle;
import com.drivio.driver.domain.model.LicenseVerificationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ManageDriverOnboardingService implements ManageDriverOnboardingUseCase {

    private final CurrentDriverProvider currentDriverProvider;
    private final DriverRepository driverRepository;
    private final DriverLicenseVerifier licenseVerifier;
    private final Clock clock;

    public ManageDriverOnboardingService(
            CurrentDriverProvider currentDriverProvider,
            DriverRepository driverRepository,
            DriverLicenseVerifier licenseVerifier,
            Clock clock
    ) {
        this.currentDriverProvider = currentDriverProvider;
        this.driverRepository = driverRepository;
        this.licenseVerifier = licenseVerifier;
        this.clock = clock;
    }

    @Override
    @Transactional
    public DriverProfileView getMyProfile() {
        CurrentDriver driver = currentDriverProvider.getCurrentDriver();
        DriverProfile profile = getOrCreateProfile(driver.id());
        boolean hasActiveVehicle = driverRepository.findVehicles(driver.id()).stream()
            .anyMatch(DriverVehicle::isActive);
        if (profile.online() && !profile.canGoOnline(hasActiveVehicle, LocalDate.now(clock))) {
            profile = driverRepository.saveProfile(
                profile.setOnline(false, hasActiveVehicle, LocalDate.now(clock))
            );
        }
        return toView(profile);
    }

    @Override
    @Transactional
    public DriverProfileView verifyMyLicense(VerifyDriverLicenseCommand command) {
        CurrentDriver driver = currentDriverProvider.getCurrentDriver();
        DriverProfile profile = getOrCreateProfile(driver.id());
        LicenseVerificationResult result = licenseVerifier.verify(
                driver.cpf(),
                command.licenseNumber()
        );
        return toView(driverRepository.saveProfile(profile.recordLicenseVerification(result)));
    }

    @Override
    @Transactional
    public DriverVehicleView registerMyVehicle(RegisterDriverVehicleCommand command) {
        UUID driverId = currentDriverProvider.getCurrentDriver().id();
        getOrCreateProfile(driverId);
        DriverVehicle vehicle = DriverVehicle.create(
                driverId,
                command.plate(),
                command.make(),
                command.model(),
                command.year(),
                command.category()
        );
        return toView(driverRepository.saveVehicle(vehicle));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverVehicleView> listMyVehicles() {
        UUID driverId = currentDriverProvider.getCurrentDriver().id();
        return driverRepository.findVehicles(driverId).stream()
                .map(ManageDriverOnboardingService::toView)
                .toList();
    }

    @Override
    @Transactional
    public DriverProfileView changeAvailability(DriverAvailabilityCommand command) {
        UUID driverId = currentDriverProvider.getCurrentDriver().id();
        DriverProfile profile = driverRepository.findProfile(driverId)
                .orElseThrow(() -> new IllegalStateException("Driver onboarding profile not found"));
        boolean hasActiveVehicle = driverRepository.findVehicles(driverId).stream()
                .anyMatch(DriverVehicle::isActive);
        DriverProfile updated = profile.setOnline(
            command.online(), hasActiveVehicle, LocalDate.now(clock)
        );
        return toView(driverRepository.saveProfile(updated));
    }

    private DriverProfile getOrCreateProfile(UUID driverId) {
        return driverRepository.findProfile(driverId)
                .orElseGet(() -> driverRepository.saveProfile(DriverProfile.create(driverId)));
    }

    private static DriverProfileView toView(DriverProfile profile) {
        return new DriverProfileView(
                profile.driverId(),
                profile.status(),
                profile.licenseStatus(),
                profile.licenseValidUntil(),
                profile.online(),
                profile.status() == com.drivio.driver.domain.model.DriverStatus.APPROVED
        );
    }

    private static DriverVehicleView toView(DriverVehicle vehicle) {
        return new DriverVehicleView(
                vehicle.id(),
                vehicle.plate(),
                vehicle.make(),
                vehicle.model(),
                vehicle.year(),
                vehicle.category(),
                vehicle.isActive()
        );
    }
}