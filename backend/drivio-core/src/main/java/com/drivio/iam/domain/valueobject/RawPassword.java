package com.drivio.iam.domain.valueobject;

import com.drivio.iam.domain.exception.InvalidPasswordException;
import com.drivio.iam.domain.exception.InvalidRawPasswordException;

public record RawPassword(String value) {
    public RawPassword {
        value = normalize(value);
        requiredValid(value);
    }

    private static String normalize(String value) {
        if (value == null)
            throw new InvalidRawPasswordException("Password cannot be null");

        return value.trim();
    }

    private static void requiredValid(String value) {
        if (value.isBlank())
            throw new InvalidPasswordException("Password cannot be blank");

        if (value.length() < 8)
            throw new InvalidPasswordException("Password must be at least 8 characters long");

        if (value.length() > 128)
            throw new InvalidPasswordException("Password must be at most 128 characters long");
    }
}
