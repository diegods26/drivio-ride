package com.drivio.driver.domain.exception;

public final class AdministratorAccessDeniedException extends RuntimeException {

    public AdministratorAccessDeniedException() {
        super("Administrator access denied");
    }
}