package com.drivio.passenger.domain.valueobject;

import java.util.Objects;

public record PhoneNumber(String value) {

    public PhoneNumber {
        Objects.requireNonNull(value, "Phone number is required");
        if (!value.matches("\\+[1-9][0-9]{7,14}")) {
            throw new IllegalArgumentException("Phone number must use E.164 format");
        }
    }
}