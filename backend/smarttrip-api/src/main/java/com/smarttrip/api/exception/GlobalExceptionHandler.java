package com.smarttrip.api.exception;

import com.smarttrip.api.integration.foursquare.FoursquareApiException;
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
    public ProblemDetail handleIllegalArgumentException(
            IllegalArgumentException exception) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage()
                );

        problemDetail.setTitle("Invalid request");

        return problemDetail;
    }

    @ExceptionHandler(FoursquareApiException.class)
    public ResponseEntity<ProblemDetail> handleFoursquareApiException(
            FoursquareApiException exception
    ) {
        HttpStatus status;

        if (exception.getStatusCode() == 401) {
            status = HttpStatus.BAD_GATEWAY;
        } else if (exception.getStatusCode() == 429) {
            status = HttpStatus.TOO_MANY_REQUESTS;
        } else if (exception.getStatusCode() >= 500) {
            status = HttpStatus.BAD_GATEWAY;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle("Foursquare API error");
        problem.setDetail(exception.getMessage());

        return ResponseEntity
                .status(status)
                .body(problem);
    }
}