package com.drivio.iam.domain.exception;

public final class InvalidCpfException extends DomainException {
    public InvalidCpfException(String cpf) {
        super("Invalid CPF: " + cpf);
    }
}
