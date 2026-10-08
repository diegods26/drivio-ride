package com.drivio.driver.domain.exception;

public final class DriverAccessDeniedException extends RuntimeException {

    public DriverAccessDeniedException() {
        super("Driver access denied");
    }
}