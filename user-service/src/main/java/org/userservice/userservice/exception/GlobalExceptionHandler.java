package org.userservice.userservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleDeviceNotFoundException(UserNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("User Not Found");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}