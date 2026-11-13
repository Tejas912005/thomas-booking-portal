package com.thomascook.hotel.exception;

import com.thomascook.hotel.dto.ErrorResponse;
import com.thomascook.hotel.dto.FieldError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HotelNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHotelNotFound(
            HotelNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error("Hotel Not Found")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .fieldErrors(List.of())
                        .build()
        );
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFound(
            BookingNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error("Booking Not Found")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .fieldErrors(List.of())
                        .build()
        );
    }

    @ExceptionHandler(NoAvailabilityException.class)
    public ResponseEntity<ErrorResponse> handleNoAvailability(
            NoAvailabilityException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CONFLICT.value())
                        .error("No Availability")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .fieldErrors(List.of())
                        .nearestDates(ex.getAlternatives())
                        .build()
        );
    }

    @ExceptionHandler(AlreadyCancelledException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyCancelled(
            AlreadyCancelledException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CONFLICT.value())
                        .error("Already Cancelled")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .fieldErrors(List.of())
                        .build()
        );
    }

    @ExceptionHandler(InvalidBookingDatesException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDates(
            InvalidBookingDatesException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Invalid Booking Dates")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .fieldErrors(List.of())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> FieldError.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Validation Failed")
                        .message("Request validation failed")
                        .path(request.getRequestURI())
                        .fieldErrors(fieldErrors)
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error("Internal Server Error")
                        .message("An unexpected error occurred")
                        .path(request.getRequestURI())
                        .fieldErrors(List.of())
                        .build()
        );
    }
}
