package com.drivio.iam.domain.exception;

import com.drivio.iam.domain.valueobject.Email;

public final class EmailAlreadyExistsException extends DomainException {

    public EmailAlreadyExistsException(Email email) {
        super("Email already exists: " + email.value());
    }
}
