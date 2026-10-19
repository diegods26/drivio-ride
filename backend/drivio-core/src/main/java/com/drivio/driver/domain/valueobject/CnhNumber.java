package com.drivio.driver.domain.valueobject;

import java.util.Objects;

public final class CnhNumber {

    private final String value;

    public CnhNumber(String value) {
        Objects.requireNonNull(value, "Driver license number is required");
        if (!value.matches("[0-9]{11}")) {
            throw new IllegalArgumentException("Driver license number format is invalid");
        }
        this.value = value;
    }

    public String valueForVerification() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof CnhNumber cnhNumber && value.equals(cnhNumber.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "CnhNumber[REDACTED]";
    }
}