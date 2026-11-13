package com.thomascook.hotel.controller;

import com.thomascook.hotel.dto.*;
import com.thomascook.hotel.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/quote")
    public ResponseEntity<BookingQuoteResponse> quote(@Valid @RequestBody BookingQuoteRequest request) {
        return ResponseEntity.ok(bookingService.quote(request));
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/lookup")
    public ResponseEntity<BookingResponse> lookupBooking(
            @RequestParam String reference,
            @RequestParam String email) {
        return ResponseEntity.ok(bookingService.lookupBooking(reference, email));
    }

    @PostMapping("/{reference}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable String reference,
            @Valid @RequestBody CancelBookingRequest request) {
        return ResponseEntity.ok(bookingService.cancelBooking(reference, request.getEmail()));
    }
}
