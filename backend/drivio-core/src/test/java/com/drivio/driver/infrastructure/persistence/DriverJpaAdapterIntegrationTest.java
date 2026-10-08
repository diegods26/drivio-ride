package com.drivio.driver.infrastructure.persistence;

import com.drivio.driver.application.port.out.DriverRepository;
import com.drivio.driver.domain.model.DriverProfile;
import com.drivio.driver.domain.model.DriverLicenseStatus;
import com.drivio.driver.domain.model.DriverStatus;
import com.drivio.driver.domain.model.DriverVehicle;
import com.drivio.driver.domain.model.LicenseVerificationResult;
import com.drivio.driver.domain.model.VehicleCategory;
import com.drivio.iam.domain.model.Role;
import com.drivio.iam.infrastructure.persistence.jpa.UserJpaRepository;
import com.drivio.iam.infrastructure.persistence.jpa.entity.UserJpaEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class DriverJpaAdapterIntegrationTest {

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

        @Autowired
        private EntityManager entityManager;

    @Test
    void shouldPersistDriverProfileAndVehicleWithoutPersistingLicenseNumber() {
        UUID driverId = createDriver();
        DriverProfile profile = DriverProfile.create(driverId);
        driverRepository.saveProfile(profile);

        DriverVehicle vehicle = DriverVehicle.create(
                driverId,
                "ABC1D23",
                "Toyota",
                "Corolla",
                LocalDate.now().getYear(),
                VehicleCategory.STANDARD
        );
        driverRepository.saveVehicle(vehicle);

        assertThat(driverRepository.findProfile(driverId))
                .get()
                .extracting(DriverProfile::status)
                .isEqualTo(DriverStatus.ONBOARDING);
        assertThat(driverRepository.findVehicles(driverId))
                .containsExactly(vehicle);
        assertThat(vehicle.isActive()).isFalse();

        Integer cnhColumns = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns "
                        + "WHERE table_schema = current_schema() "
                        + "AND table_name = 'driver_profiles' "
                        + "AND column_name ILIKE '%license%number%'",
                Integer.class
        );
        assertThat(cnhColumns).isZero();
    }

    @Test
    void databaseShouldRejectOnlineStateWithoutApprovalAndVerifiedLicense() {
        UUID driverId = createDriver();
        driverRepository.saveProfile(DriverProfile.create(driverId));
        entityManager.flush();

        assertThatThrownBy(() -> jdbcTemplate.update(
                "UPDATE driver_profiles SET is_online = TRUE WHERE driver_id = ?",
                driverId
        )).hasMessageContaining("ck_driver_profiles_online_eligibility");
    }

    @Test
    void shouldPersistAdminApprovalAuditForDriverAndVehicle() {
        UUID driverId = createDriver();
        UUID administratorId = createAdministrator();
        DriverProfile verified = DriverProfile.create(driverId).recordLicenseVerification(
                LicenseVerificationResult.valid(
                        LocalDate.now().plusYears(1), "authority-reference-789"
                )
        );
        driverRepository.saveProfile(verified);
        DriverVehicle pendingVehicle = DriverVehicle.create(
                driverId,
                "XYZ1A23",
                "Honda",
                "Civic",
                LocalDate.now().getYear(),
                VehicleCategory.COMFORT
        );
        driverRepository.saveVehicle(pendingVehicle);

        DriverVehicle activeVehicle = pendingVehicle.activate(administratorId);
        driverRepository.saveVehicle(activeVehicle);
        DriverProfile approvedProfile = verified.approve(
                administratorId,
                true,
                LocalDate.now()
        );
        driverRepository.saveProfile(approvedProfile);

        assertThat(driverRepository.findProfile(driverId))
                .get()
                .satisfies(saved -> {
                    assertThat(saved.status()).isEqualTo(DriverStatus.APPROVED);
                    assertThat(saved.approvedBy()).isEqualTo(administratorId);
                    assertThat(saved.approvedAt()).isNotNull();
                });
        assertThat(driverRepository.findVehicle(driverId, pendingVehicle.id()))
                .get()
                .satisfies(saved -> {
                    assertThat(saved.isActive()).isTrue();
                    assertThat(saved.approvedBy()).isEqualTo(administratorId);
                    assertThat(saved.approvedAt()).isNotNull();
                });
    }

    @Test
    void databaseShouldRejectActiveVehicleWithoutApprovalAudit() {
        UUID driverId = createDriver();
        driverRepository.saveProfile(DriverProfile.create(driverId));
        UUID vehicleId = UUID.randomUUID();

        assertThatThrownBy(() -> jdbcTemplate.update(
                "INSERT INTO driver_vehicles "
                        + "(id, driver_id, plate, make, model, model_year, category, is_active) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, TRUE)",
                vehicleId,
                driverId,
                "QWE1R23",
                "Toyota",
                "Yaris",
                LocalDate.now().getYear(),
                "STANDARD"
        )).hasMessageContaining("ck_driver_vehicles_approval_audit");
    }

    @Test
    void shouldPersistManualLicenseRejectionDecisionAndAudit() {
        UUID driverId = createDriver();
        UUID administratorId = createAdministrator();
        DriverProfile rejectedProfile = DriverProfile.create(driverId)
                .recordManualLicenseReview(
                        administratorId,
                        false,
                        null,
                        "CNH vencida após consulta manual à autoridade de trânsito",
                        LocalDate.now()
                );

        driverRepository.saveProfile(rejectedProfile);

        assertThat(driverRepository.findProfile(driverId))
                .get()
                .satisfies(saved -> {
                    assertThat(saved.status()).isEqualTo(DriverStatus.REJECTED);
                    assertThat(saved.licenseStatus()).isEqualTo(DriverLicenseStatus.INVALID);
                    assertThat(saved.licenseReviewedBy()).isEqualTo(administratorId);
                    assertThat(saved.licenseReviewedAt()).isNotNull();
                    assertThat(saved.licenseReviewReason())
                            .isEqualTo("CNH vencida após consulta manual à autoridade de trânsito");
                    assertThat(saved.toString()).doesNotContain("41134746059");
                });
    }

    @Test
    void shouldPersistManualLicenseApprovalAndAllowFinalDriverApproval() {
        UUID driverId = createDriver();
        UUID administratorId = createAdministrator();
        DriverProfile manuallyReviewed = DriverProfile.create(driverId)
                .recordManualLicenseReview(
                        administratorId,
                        true,
                        LocalDate.now().plusYears(1),
                        "CNH conferida manualmente com a autoridade",
                        LocalDate.now()
                );
        driverRepository.saveProfile(manuallyReviewed);

        DriverVehicle activeVehicle = DriverVehicle.create(
                driverId,
                "LMN1P23",
                "Honda",
                "Civic",
                LocalDate.now().getYear(),
                VehicleCategory.COMFORT
        ).activate(administratorId);
        driverRepository.saveVehicle(activeVehicle);

        DriverProfile approved = manuallyReviewed.approve(
                administratorId,
                true,
                LocalDate.now()
        );
        driverRepository.saveProfile(approved);

        assertThat(driverRepository.findProfile(driverId))
                .get()
                .satisfies(saved -> {
                    assertThat(saved.status()).isEqualTo(DriverStatus.APPROVED);
                    assertThat(saved.licenseReviewReason())
                            .isEqualTo("CNH conferida manualmente com a autoridade");
                    assertThat(saved.licenseReviewedBy()).isEqualTo(administratorId);
                    assertThat(saved.licenseReviewedAt()).isNotNull();
                    assertThat(saved.approvedBy()).isEqualTo(administratorId);
                    assertThat(saved.approvedAt()).isNotNull();
                });
    }

    private UUID createDriver() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        userRepository.save(new UserJpaEntity(
                id,
                "Test Driver",
                "driver-" + id + "@example.com",
                "41134746059",
                "encoded-password",
                Role.DRIVER,
                true,
                now,
                now
        ));
        return id;
    }

        private UUID createAdministrator() {
                UUID id = UUID.randomUUID();
                Instant now = Instant.now();
                userRepository.save(new UserJpaEntity(
                                id,
                                "Test Administrator",
                                "admin-" + id + "@example.com",
                                "52998224725",
                                "encoded-password",
                                Role.ADMIN,
                                true,
                                now,
                                now
                ));
                return id;
        }
}
