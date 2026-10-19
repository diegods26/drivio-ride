package com.drivio.passenger.infrastructure.web.controller;

import com.drivio.passenger.application.dto.PassengerAddressView;
import com.drivio.passenger.application.dto.PassengerProfileView;
import com.drivio.passenger.application.dto.SavePassengerAddressCommand;
import com.drivio.passenger.application.dto.UpdatePassengerProfileCommand;
import com.drivio.passenger.application.port.in.ManagePassengerProfileUseCase;
import com.drivio.passenger.infrastructure.web.dto.SavePassengerAddressRequest;
import com.drivio.passenger.infrastructure.web.dto.UpdatePassengerProfileRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/passengers/me")
public class PassengerProfileController {

    private final ManagePassengerProfileUseCase managePassengerProfileUseCase;

    public PassengerProfileController(
            ManagePassengerProfileUseCase managePassengerProfileUseCase
    ) {
        this.managePassengerProfileUseCase = managePassengerProfileUseCase;
    }

    @GetMapping
    public PassengerProfileView getMyProfile() {
        return managePassengerProfileUseCase.getMyProfile();
    }

    @PutMapping
    public PassengerProfileView updateMyProfile(
            @Valid @RequestBody UpdatePassengerProfileRequest request
    ) {
        return managePassengerProfileUseCase.updateMyProfile(
                new UpdatePassengerProfileCommand(request.phoneNumber())
        );
    }

    @GetMapping("/addresses")
    public List<PassengerAddressView> listMyAddresses() {
        return managePassengerProfileUseCase.listMyAddresses();
    }

    @PostMapping("/addresses")
    public ResponseEntity<PassengerAddressView> addMyAddress(
            @Valid @RequestBody SavePassengerAddressRequest request
    ) {
        PassengerAddressView address = managePassengerProfileUseCase.addMyAddress(
                new SavePassengerAddressCommand(
                        request.label(),
                        request.street(),
                        request.city(),
                        request.state(),
                        request.postalCode()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> removeMyAddress(@PathVariable UUID addressId) {
        if (!managePassengerProfileUseCase.removeMyAddress(addressId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}