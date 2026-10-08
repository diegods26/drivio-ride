package com.drivio.driver.infrastructure.web;

import com.drivio.driver.domain.exception.AdministratorAccessDeniedException;
import com.drivio.driver.domain.exception.DriverAccessDeniedException;
import com.drivio.iam.infrastructure.web.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice(basePackageClasses = DriverExceptionHandler.class)
public class DriverExceptionHandler {

    @ExceptionHandler({DriverAccessDeniedException.class, AdministratorAccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDenied() {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "DRIVER_ACCESS_DENIED",
                "Driver operation is not authorized",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState() {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "DRIVER_STATE_CONFLICT",
                "Driver operation is not allowed in the current state",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInput() {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_DRIVER_INPUT",
                "Driver input is invalid",
                Instant.now()
        );
        return ResponseEntity.badRequest().body(response);
    }
}