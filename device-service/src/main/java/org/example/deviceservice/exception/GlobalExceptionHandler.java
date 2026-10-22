package org.example.deviceservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DeviceNotFoundException.class)
    public ProblemDetail handleDeviceNotFoundException(DeviceNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Device Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}