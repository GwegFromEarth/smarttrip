package com.smarttrip.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TripNotFoundException.class)
    public ProblemDetail handleTripNotFound(TripNotFoundException exception) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND,
                        exception.getMessage()
                );

        problemDetail.setTitle("Trip not found");

        return problemDetail;
    }

    @ExceptionHandler(InvalidTripException.class)
    public ProblemDetail handleInvalidTrip(InvalidTripException exception) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage()
                );

        problemDetail.setTitle("Invalid trip");

        return problemDetail;
    }

    @ExceptionHandler(ItineraryNotFoundException.class)
    public ProblemDetail handleItineraryNotFound(
            ItineraryNotFoundException exception) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND,
                        exception.getMessage()
                );

        problemDetail.setTitle("Itinerary not found");

        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(
            IllegalArgumentException exception
    ) {
        return ResponseEntity
                .badRequest()
                .body(exception.getMessage());
    }
}