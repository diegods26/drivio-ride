package com.drivio.driver.application;

import com.drivio.driver.application.port.out.CurrentAdministratorProvider;
import com.drivio.driver.application.port.out.DriverRepository;
import com.drivio.driver.application.usecase.ApproveDriverService;
import com.drivio.driver.application.dto.ManualDriverLicenseReviewCommand;
import com.drivio.driver.domain.model.DriverLicenseStatus;
import com.drivio.driver.domain.model.DriverStatus;
import com.drivio.driver.domain.model.DriverProfile;
import com.drivio.driver.domain.model.DriverVehicle;
import com.drivio.driver.domain.model.LicenseVerificationResult;
import com.drivio.driver.domain.model.VehicleCategory;
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

class ApproveDriverServiceTest {

    private final UUID administratorId = UUID.randomUUID();
    private final UUID driverId = UUID.randomUUID();
    private CurrentAdministratorProvider administratorProvider;
    private DriverRepository driverRepository;
    private ApproveDriverService service;

    @BeforeEach
    void setUp() {
        administratorProvider = mock(CurrentAdministratorProvider.class);
        driverRepository = mock(DriverRepository.class);
        service = new ApproveDriverService(
                administratorProvider,
                driverRepository,
                Clock.fixed(Instant.parse("2026-10-08T00:00:00Z"), ZoneOffset.UTC)
        );
        when(administratorProvider.getCurrentAdministratorId()).thenReturn(administratorId);
        DriverProfile verified = DriverProfile.create(driverId).recordLicenseVerification(
                LicenseVerificationResult.valid(
                        LocalDate.now().plusYears(1), "authority-reference-123"
                )
        );
        when(driverRepository.findProfile(driverId)).thenReturn(Optional.of(verified));
        when(driverRepository.saveProfile(any(DriverProfile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldApproveVerifiedDriverOnlyAfterActiveVehicleExists() {
        when(driverRepository.findVehicles(driverId)).thenReturn(List.of(
                DriverVehicle.create(
                        driverId,
                        "ABC1D23",
                        "Toyota",
                        "Corolla",
                        LocalDate.now().getYear(),
                        VehicleCategory.STANDARD
                ).activate(administratorId)
        ));

        var profile = service.approve(driverId);

        assertThat(profile.approved()).isTrue();
        verify(administratorProvider).getCurrentAdministratorId();
        verify(driverRepository).saveProfile(argThat(saved -> saved.status().name().equals("APPROVED")));
    }

    @Test
    void shouldNotApproveWithoutActiveVehicle() {
        when(driverRepository.findVehicles(driverId)).thenReturn(List.of());

        assertThatThrownBy(() -> service.approve(driverId))
                .isInstanceOf(IllegalStateException.class);

        verify(administratorProvider).getCurrentAdministratorId();
        verify(driverRepository, never()).saveProfile(any(DriverProfile.class));
    }

    @Test
    void shouldRecordManualApprovalFromAdministratorReview() {
        when(driverRepository.findProfile(driverId))
                .thenReturn(Optional.of(DriverProfile.create(driverId)));

        var reviewed = service.reviewLicense(
                driverId,
                new ManualDriverLicenseReviewCommand(
                        true,
                        LocalDate.now().plusYears(1),
                        "CNH consultada manualmente em canal oficial"
                )
        );

        assertThat(reviewed.status()).isEqualTo(DriverStatus.PENDING_APPROVAL);
        assertThat(reviewed.licenseStatus()).isEqualTo(DriverLicenseStatus.VERIFIED);
        assertThat(reviewed.licenseReviewReason())
                .isEqualTo("CNH consultada manualmente em canal oficial");
        assertThat(reviewed.licenseReviewReason()).doesNotContain("41134746059");
        assertThat(reviewed.toString()).doesNotContain("CNH consultada manualmente");
        verify(administratorProvider).getCurrentAdministratorId();
        verify(driverRepository).saveProfile(argThat(saved ->
                saved.licenseReviewedBy().equals(administratorId)
                        && saved.licenseReviewedAt() != null
        ));
    }

    @Test
    void shouldRejectDriverAndKeepReasonWhenAdministratorFindsInvalidLicense() {
        when(driverRepository.findProfile(driverId))
                .thenReturn(Optional.of(DriverProfile.create(driverId)));

        var reviewed = service.reviewLicense(
                driverId,
                new ManualDriverLicenseReviewCommand(
                        false,
                        null,
                        "CNH vencida conforme consulta manual"
                )
        );

        assertThat(reviewed.status()).isEqualTo(DriverStatus.REJECTED);
        assertThat(reviewed.licenseStatus()).isEqualTo(DriverLicenseStatus.INVALID);
        assertThat(reviewed.approved()).isFalse();
        assertThat(reviewed.licenseReviewReason()).contains("vencida");
        verify(driverRepository).saveProfile(argThat(saved ->
                saved.status() == DriverStatus.REJECTED
                        && saved.licenseReviewedBy().equals(administratorId)
                        && saved.licenseReviewedAt() != null
        ));
    }
}
