package com.drivio.iam.domain.exception;

import com.drivio.iam.domain.valueobject.Cpf;

public final class CpfAlreadyExistsException extends DomainException {
    public CpfAlreadyExistsException(Cpf cpf) {
        super("CPF already exists: " + cpf.value());
    }
}
