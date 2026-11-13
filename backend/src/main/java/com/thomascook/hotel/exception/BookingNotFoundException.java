package com.thomascook.hotel.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException() {
        super("Booking not found");
    }
    public BookingNotFoundException(String reference) {
        super("Booking not found with reference: " + reference);
    }
}
