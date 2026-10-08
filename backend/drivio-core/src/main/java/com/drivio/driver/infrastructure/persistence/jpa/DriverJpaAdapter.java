package com.drivio.driver.infrastructure.persistence.jpa;

import com.drivio.driver.application.port.out.DriverRepository;
import com.drivio.driver.domain.model.DriverProfile;
import com.drivio.driver.domain.model.DriverVehicle;
import com.drivio.driver.infrastructure.persistence.jpa.entity.DriverProfileJpaEntity;
import com.drivio.driver.infrastructure.persistence.jpa.entity.DriverVehicleJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DriverJpaAdapter implements DriverRepository {

    private final DriverProfileJpaRepository profileRepository;
    private final DriverVehicleJpaRepository vehicleRepository;

    public DriverJpaAdapter(
            DriverProfileJpaRepository profileRepository,
            DriverVehicleJpaRepository vehicleRepository
    ) {
        this.profileRepository = profileRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Optional<DriverProfile> findProfile(UUID driverId) {
        return profileRepository.findById(driverId).map(DriverJpaAdapter::toDomain);
    }

    @Override
    public DriverProfile saveProfile(DriverProfile profile) {
        DriverProfileJpaEntity entity = new DriverProfileJpaEntity(
                profile.driverId(),
                profile.status(),
                profile.online(),
                profile.approvedBy(),
                profile.approvedAt(),
                profile.licenseStatus(),
                profile.licenseValidUntil(),
                profile.verificationProvider(),
                profile.verificationReference(),
                profile.licenseVerifiedAt(),
                profile.createdAt(),
                profile.updatedAt()
        );
        return toDomain(profileRepository.save(entity));
    }

    @Override
    public List<DriverVehicle> findVehicles(UUID driverId) {
        return vehicleRepository.findByDriverIdOrderByPlate(driverId).stream()
                .map(DriverJpaAdapter::toDomain)
                .toList();
    }

    @Override
    public Optional<DriverVehicle> findVehicle(UUID driverId, UUID vehicleId) {
        return vehicleRepository.findByDriverIdAndId(driverId, vehicleId)
                .map(DriverJpaAdapter::toDomain);
    }

    @Override
    public Optional<DriverVehicle> findVehicleById(UUID vehicleId) {
        return vehicleRepository.findById(vehicleId).map(DriverJpaAdapter::toDomain);
    }

    @Override
    public DriverVehicle saveVehicle(DriverVehicle vehicle) {
        DriverVehicleJpaEntity entity = new DriverVehicleJpaEntity(
                vehicle.id(),
                vehicle.driverId(),
                vehicle.plate(),
                vehicle.make(),
                vehicle.model(),
                vehicle.year(),
                vehicle.category(),
                vehicle.isActive(),
                vehicle.approvedBy(),
                vehicle.approvedAt()
        );
        return toDomain(vehicleRepository.save(entity));
    }

    private static DriverProfile toDomain(DriverProfileJpaEntity entity) {
        return new DriverProfile(
                entity.getDriverId(),
                entity.getStatus(),
                entity.isOnline(),
                entity.getApprovedBy(),
                entity.getApprovedAt(),
                entity.getLicenseStatus(),
                entity.getLicenseValidUntil(),
                entity.getVerificationProvider(),
                entity.getVerificationReference(),
                entity.getLicenseVerifiedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private static DriverVehicle toDomain(DriverVehicleJpaEntity entity) {
        return new DriverVehicle(
                entity.getId(),
                entity.getDriverId(),
                entity.getPlate(),
                entity.getMake(),
                entity.getModel(),
                entity.getYear(),
                entity.getCategory(),
                entity.isActive(),
                entity.getApprovedBy(),
                entity.getApprovedAt()
        );
    }
}