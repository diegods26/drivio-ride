package com.drivio.iam.domain.valueobject;

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
            throw new InvalidRawPasswordException("Password cannot be blank");

        if (value.length() < 8)
            throw new InvalidRawPasswordException("Password must be at least 8 characters long");

        if (value.length() > 128)
            throw new InvalidRawPasswordException("Password must be at most 128 characters long");
    }
}
