package com.drivio.driver.application;

import com.drivio.driver.application.dto.CurrentDriver;
import com.drivio.driver.application.dto.DriverAvailabilityCommand;
import com.drivio.driver.application.dto.RegisterDriverVehicleCommand;
import com.drivio.driver.application.dto.VerifyDriverLicenseCommand;
import com.drivio.driver.application.port.out.CurrentDriverProvider;
import com.drivio.driver.application.port.out.DriverLicenseVerifier;
import com.drivio.driver.application.port.out.DriverRepository;
import com.drivio.driver.application.usecase.ManageDriverOnboardingService;
import com.drivio.driver.domain.model.DriverLicenseStatus;
import com.drivio.driver.domain.model.DriverProfile;
import com.drivio.driver.domain.model.DriverVehicle;
import com.drivio.driver.domain.model.LicenseVerificationResult;
import com.drivio.driver.domain.model.VehicleCategory;
import com.drivio.driver.domain.valueobject.CnhNumber;
import com.drivio.iam.domain.valueobject.Cpf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ManageDriverOnboardingServiceTest {

    private final UUID driverId = UUID.randomUUID();
    private CurrentDriverProvider currentDriverProvider;
    private DriverRepository driverRepository;
    private DriverLicenseVerifier licenseVerifier;
    private ManageDriverOnboardingService service;

    @BeforeEach
    void setUp() {
        currentDriverProvider = mock(CurrentDriverProvider.class);
        driverRepository = mock(DriverRepository.class);
        licenseVerifier = mock(DriverLicenseVerifier.class);
        service = new ManageDriverOnboardingService(
                currentDriverProvider,
                driverRepository,
                licenseVerifier,
                Clock.fixed(Instant.parse("2026-10-08T00:00:00Z"), ZoneOffset.UTC)
        );
        when(currentDriverProvider.getCurrentDriver())
                .thenReturn(new CurrentDriver(driverId, new Cpf("41134746059")));
        when(driverRepository.findProfile(driverId)).thenReturn(Optional.empty());
        when(driverRepository.findVehicles(driverId)).thenReturn(List.of());
        when(driverRepository.saveProfile(any(DriverProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldKeepDriverInOnboardingWhenAuthorizedProviderIsUnavailable() {
        when(licenseVerifier.verify(any(), any()))
                .thenReturn(LicenseVerificationResult.unavailable(
                        "AUTHORIZED_PROVIDER_NOT_CONFIGURED"
                ));

        var profile = service.verifyMyLicense(
                new VerifyDriverLicenseCommand(new CnhNumber("12345678901"))
        );

        assertThat(profile.licenseStatus()).isEqualTo(DriverLicenseStatus.UNAVAILABLE);
        assertThat(profile.approved()).isFalse();
        assertThat(profile.online()).isFalse();
        verify(licenseVerifier).verify(any(Cpf.class), any(CnhNumber.class));
        verify(driverRepository).saveProfile(argThat(saved ->
                saved.driverId().equals(driverId)
                        && saved.licenseStatus() == DriverLicenseStatus.UNAVAILABLE
        ));
    }

    @Test
    void shouldNotAllowOnlineUntilDriverHasAnApprovedProfileAndVehicle() {
        DriverProfile pending = DriverProfile.create(driverId);
        when(driverRepository.findProfile(driverId)).thenReturn(Optional.of(pending));
        when(driverRepository.findVehicles(driverId)).thenReturn(List.of());

        assertThatThrownBy(() -> service.changeAvailability(
                new DriverAvailabilityCommand(true)))
                .isInstanceOf(IllegalStateException.class);

        verify(driverRepository, never()).saveProfile(any(DriverProfile.class));
    }

    @Test
    void shouldRegisterVehicleOnlyForAuthenticatedDriver() {
        when(driverRepository.saveVehicle(any(DriverVehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var vehicle = service.registerMyVehicle(new RegisterDriverVehicleCommand(
                "ABC1D23", "Toyota", "Corolla", LocalDate.now().getYear(),
                VehicleCategory.STANDARD
        ));

        assertThat(vehicle.plate()).isEqualTo("ABC1D23");
        verify(driverRepository).saveProfile(argThat(profile ->
                profile.driverId().equals(driverId)
                        && profile.status().name().equals("ONBOARDING")
        ));
        verify(driverRepository).saveVehicle(argThat(saved ->
                saved.driverId().equals(driverId)
        ));
    }

    @Test
    void shouldTakeExpiredOnlineDriverOffline() {
        Instant now = Instant.parse("2026-10-08T00:00:00Z");
        DriverProfile expiredOnline = new DriverProfile(
                driverId,
                com.drivio.driver.domain.model.DriverStatus.APPROVED,
                true,
                UUID.randomUUID(),
                now.minusSeconds(60),
                DriverLicenseStatus.VERIFIED,
                LocalDate.of(2026, 10, 7),
                "AUTHORIZED_PROVIDER",
                "authority-ref-123",
                now.minusSeconds(3600),
                now.minusSeconds(7200),
                now.minusSeconds(60)
        );
        when(driverRepository.findProfile(driverId)).thenReturn(Optional.of(expiredOnline));
        when(driverRepository.findVehicles(driverId)).thenReturn(List.of(
                DriverVehicle.create(
                        driverId, "ABC1D23", "Toyota", "Corolla", 2024,
                        VehicleCategory.STANDARD
                ).activate(UUID.randomUUID())
        ));
        when(driverRepository.saveProfile(any(DriverProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var view = service.getMyProfile();

        assertThat(view.online()).isFalse();
        verify(driverRepository).saveProfile(argThat(saved -> !saved.online()));
    }
}
