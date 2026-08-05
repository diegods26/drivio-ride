package com.drivio.iam.domain.exception;

public final class InvalidEmailException extends DomainException {
    public InvalidEmailException(String email) {
        super("Invalid email: " + email);
    }
}
