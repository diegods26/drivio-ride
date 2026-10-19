package com.drivio.passenger.infrastructure.persistence.jpa;

import com.drivio.passenger.application.port.out.PassengerProfileRepository;
import com.drivio.passenger.domain.model.PassengerAddress;
import com.drivio.passenger.domain.model.PassengerProfile;
import com.drivio.passenger.domain.valueobject.PhoneNumber;
import com.drivio.passenger.infrastructure.persistence.jpa.entity.PassengerAddressJpaEntity;
import com.drivio.passenger.infrastructure.persistence.jpa.entity.PassengerProfileJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PassengerProfileJpaAdapter implements PassengerProfileRepository {

    private final PassengerProfileJpaRepository profileRepository;
    private final PassengerAddressJpaRepository addressRepository;

    public PassengerProfileJpaAdapter(
            PassengerProfileJpaRepository profileRepository,
            PassengerAddressJpaRepository addressRepository
    ) {
        this.profileRepository = profileRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public Optional<PassengerProfile> findProfileByPassengerId(UUID passengerId) {
        return profileRepository.findById(passengerId).map(PassengerProfileJpaAdapter::toDomain);
    }

    @Override
    public PassengerProfile saveProfile(PassengerProfile profile) {
        PassengerProfileJpaEntity entity = new PassengerProfileJpaEntity(
                profile.passengerId(),
                profile.phoneNumber() == null ? null : profile.phoneNumber().value(),
                profile.createdAt(),
                profile.updatedAt()
        );
        return toDomain(profileRepository.save(entity));
    }

    @Override
    public List<PassengerAddress> findAddressesByPassengerId(UUID passengerId) {
        return addressRepository.findByPassengerIdOrderByCreatedAtDesc(passengerId)
                .stream()
                .map(PassengerProfileJpaAdapter::toDomain)
                .toList();
    }

    @Override
    public PassengerAddress saveAddress(PassengerAddress address) {
        PassengerAddressJpaEntity entity = new PassengerAddressJpaEntity(
                address.id(),
                address.passengerId(),
                address.label(),
                address.street(),
                address.city(),
                address.state(),
                address.postalCode(),
                address.createdAt()
        );
        return toDomain(addressRepository.save(entity));
    }

    @Override
    public boolean deleteAddress(UUID passengerId, UUID addressId) {
        return addressRepository.deleteByPassengerIdAndId(passengerId, addressId) > 0;
    }

    private static PassengerProfile toDomain(PassengerProfileJpaEntity entity) {
        PhoneNumber phoneNumber = entity.getPhoneNumber() == null
                ? null : new PhoneNumber(entity.getPhoneNumber());
        return new PassengerProfile(
                entity.getPassengerId(),
                phoneNumber,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private static PassengerAddress toDomain(PassengerAddressJpaEntity entity) {
        return new PassengerAddress(
                entity.getId(),
                entity.getPassengerId(),
                entity.getLabel(),
                entity.getStreet(),
                entity.getCity(),
                entity.getState(),
                entity.getPostalCode(),
                entity.getCreatedAt()
        );
    }
}