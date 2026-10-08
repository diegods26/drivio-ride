package com.drivio.passenger.infrastructure.web;

import com.drivio.iam.infrastructure.web.response.ErrorResponse;
import com.drivio.passenger.domain.exception.PassengerAccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class PassengerExceptionHandler {

    @ExceptionHandler(PassengerAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied() {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "PASSENGER_ACCESS_DENIED",
                "Passenger access denied",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}