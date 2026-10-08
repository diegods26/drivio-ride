package com.drivio.driver.domain;

import com.drivio.driver.domain.model.DriverLicenseStatus;
import com.drivio.driver.domain.model.DriverProfile;
import com.drivio.driver.domain.model.DriverStatus;
import com.drivio.driver.domain.model.LicenseVerificationResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DriverProfileTest {

    private static final UUID ADMINISTRATOR_ID = UUID.randomUUID();

    @Test
    void shouldStartInOnboardingAndRemainBlockedWhenAuthorityUnavailable() {
        DriverProfile profile = DriverProfile.create(UUID.randomUUID());

        DriverProfile result = profile.recordLicenseVerification(
                LicenseVerificationResult.unavailable("AUTHORIZED_PROVIDER_UNAVAILABLE")
        );

        assertThat(result.status()).isEqualTo(DriverStatus.ONBOARDING);
        assertThat(result.licenseStatus()).isEqualTo(DriverLicenseStatus.UNAVAILABLE);
        assertThat(result.canGoOnline(true, LocalDate.now())).isFalse();
    }

    @Test
    void shouldNotApproveDriverWithoutCurrentVerifiedLicense() {
        DriverProfile profile = DriverProfile.create(UUID.randomUUID());

        assertThatThrownBy(() -> profile.approve(ADMINISTRATOR_ID, true, LocalDate.now()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldRequireActiveVehicleBeforeApproval() {
        DriverProfile profile = verifiedProfile(LocalDate.now().plusYears(1));

        assertThatThrownBy(() -> profile.approve(ADMINISTRATOR_ID, false, LocalDate.now()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldEnableOnlineOnlyAfterApprovalWithValidLicenseAndVehicle() {
        DriverProfile profile = verifiedProfile(LocalDate.now().plusYears(1))
            .approve(ADMINISTRATOR_ID, true, LocalDate.now());

        assertThat(profile.status()).isEqualTo(DriverStatus.APPROVED);
        assertThat(profile.approvedBy()).isEqualTo(ADMINISTRATOR_ID);
        assertThat(profile.approvedAt()).isNotNull();
        assertThat(profile.canGoOnline(true, LocalDate.now())).isTrue();
        assertThat(profile.canGoOnline(false, LocalDate.now())).isFalse();
    }

    @Test
    void shouldBlockOnlineWhenVerifiedLicenseExpires() {
        LocalDate approvalDate = LocalDate.now();
        DriverProfile profile = verifiedProfile(approvalDate.plusDays(1))
            .approve(ADMINISTRATOR_ID, true, approvalDate);

        assertThat(profile.canGoOnline(true, approvalDate.plusDays(2))).isFalse();
    }

    @Test
    void shouldTreatProviderSuccessForAlreadyExpiredLicenseAsExpired() {
        DriverProfile profile = DriverProfile.create(UUID.randomUUID()).recordLicenseVerification(
                LicenseVerificationResult.valid(LocalDate.now().minusDays(1), "verification-ref-456")
        );

        assertThat(profile.licenseStatus()).isEqualTo(DriverLicenseStatus.EXPIRED);
        assertThat(profile.status()).isEqualTo(DriverStatus.ONBOARDING);
    }

    private static DriverProfile verifiedProfile(LocalDate validUntil) {
        return DriverProfile.create(UUID.randomUUID()).recordLicenseVerification(
                LicenseVerificationResult.valid(validUntil, "verification-ref-123")
        );
    }
}
