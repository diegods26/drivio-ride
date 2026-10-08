package com.drivio.iam.domain.exception;

public final class CpfAlreadyExistsException extends DomainException {
    public CpfAlreadyExistsException() {
        super("CPF already exists");
    }
}
