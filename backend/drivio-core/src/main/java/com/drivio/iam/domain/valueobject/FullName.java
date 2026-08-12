package com.drivio.iam.domain.valueobject;

import com.drivio.iam.domain.exception.InvalidEmailException;
import com.drivio.iam.domain.exception.InvalidFullNameException;

import java.util.regex.Pattern;

public record FullName(String value) {
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L}][\\p{L}'-]*(\\s+[\\p{L}][\\p{L}'-]*)+$");

    public FullName {
        value = normalize(value);
        requiredValid(value);
    }

    private static String normalize(String value) {
        if (value == null)
            throw new InvalidFullNameException(null);

        return value
                .trim()
                .replaceAll("\\s+", " ");
    }

    private static void requiredValid(String value) {
        if (value.isBlank())
            throw new InvalidFullNameException(value);

        if (value.length() < 2)
            throw new InvalidFullNameException(value);

        if (value.length() > 120)
            throw new InvalidFullNameException(value);

        if (!NAME_PATTERN.matcher(value).matches())
            throw new InvalidFullNameException(value);
    }
}
