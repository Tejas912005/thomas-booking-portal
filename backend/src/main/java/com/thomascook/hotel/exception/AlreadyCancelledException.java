package com.thomascook.hotel.exception;

public class AlreadyCancelledException extends RuntimeException {
    public AlreadyCancelledException() {
        super("Booking is already cancelled");
    }
}
