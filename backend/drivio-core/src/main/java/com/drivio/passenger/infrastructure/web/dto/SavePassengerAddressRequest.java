package com.drivio.passenger.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SavePassengerAddressRequest(
        @NotBlank(message = "Address label is required")
        @Size(max = 50)
        String label,

        @NotBlank(message = "Street is required")
        @Size(max = 160)
        String street,

        @NotBlank(message = "City is required")
        @Size(max = 100)
        String city,

        @NotBlank(message = "State is required")
        @Pattern(regexp = "[A-Za-z]{2}", message = "State must be a Brazilian UF code")
        String state,

        @NotBlank(message = "Postal code is required")
        @Pattern(regexp = "(?:[0-9]{8}|[0-9]{5}-[0-9]{3})", message = "Postal code must be a valid CEP")
        String postalCode
) {
}