package com.drivio.iam.domain.valueobject;

import com.drivio.iam.domain.exception.InvalidEmailException;

import java.util.Locale;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        value = normalize(value);
        requireValid(value);
    }

    private static String normalize(String value) {
        if (value == null)
            throw new InvalidEmailException(null);

        return value.trim().toLowerCase(Locale.ROOT);
    }
    private static void  requireValid(String value) {
        if (value.isBlank())
            throw new InvalidEmailException(value);

        if (!EMAIL_PATTERN.matcher(value).matches())
            throw new InvalidEmailException(value);
    }
}
