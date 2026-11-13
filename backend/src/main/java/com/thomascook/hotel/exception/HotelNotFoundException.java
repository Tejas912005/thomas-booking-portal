package com.thomascook.hotel.exception;

public class HotelNotFoundException extends RuntimeException {
    public HotelNotFoundException() {
        super("Hotel not found");
    }
    public HotelNotFoundException(Long id) {
        super("Hotel not found with id: " + id);
    }
}
