package com.drivio.iam.domain.valueobject;

import com.drivio.iam.domain.exception.InvalidCpfException;

public record Cpf(String value) {

    public Cpf {
        value = normalize(value);
        requireValid(value);
    }

    private static String normalize(String value) {
        if (value == null) {
            throw new InvalidCpfException();
        }

        String normalized = value.trim();
        if (!normalized.matches(
            "(?:\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})")) {
            throw new InvalidCpfException();
        }

        return normalized.replace(".", "").replace("-", "");
    }

    private static void requireValid(String value) {
        if (value.isBlank()) {
            throw new InvalidCpfException();
        }

        if (value.length() != 11) {
            throw new InvalidCpfException();
        }

        if (!value.matches("\\d{11}")) {
            throw new InvalidCpfException();
        }

        if (isRepeatedDigits(value)) {
            throw new InvalidCpfException();
        }

        if (!hasValidCheckDigits(value)) {
            throw new InvalidCpfException();
        }
    }

    private static boolean isRepeatedDigits(String value) {
        return value.chars().distinct().count() == 1;
    }

    private static boolean hasValidCheckDigits(String value) {
        int sum1 = 0;
        int sum2 = 0;

        for (int i = 0; i < 9; i++) {
            int digit = Character.getNumericValue(value.charAt(i));
            sum1 += digit * (10 - i);
            sum2 += digit * (11 - i);
        }

        int digit1 = 11 - (sum1 % 11);
        if (digit1 >= 10) {
            digit1 = 0;
        }

        sum2 += digit1 * 2;
        int digit2 = 11 - (sum2 % 11);
        if (digit2 >= 10) {
            digit2 = 0;
        }

        return digit1 == Character.getNumericValue(value.charAt(9))
                && digit2 == Character.getNumericValue(value.charAt(10));
    }
}
