package com.drivio.iam.domain.valueobject;

import com.drivio.iam.domain.exception.InvalidPasswordException;

public record HashedPassword(String value) {
    public HashedPassword {
        if (value == null || value.isBlank()) {
            throw new InvalidPasswordException("Hashed password cannot be null or blank");
        }
    }
}
