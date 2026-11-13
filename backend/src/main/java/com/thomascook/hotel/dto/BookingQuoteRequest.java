package com.thomascook.hotel.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class BookingQuoteRequest {

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOut;

    public BookingQuoteRequest() {}

    public BookingQuoteRequest(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public static BookingQuoteRequestBuilder builder() {
        return new BookingQuoteRequestBuilder();
    }

    public static class BookingQuoteRequestBuilder {
        private Long hotelId;
        private LocalDate checkIn;
        private LocalDate checkOut;

        public BookingQuoteRequestBuilder hotelId(Long hotelId) { this.hotelId = hotelId; return this; }
        public BookingQuoteRequestBuilder checkIn(LocalDate checkIn) { this.checkIn = checkIn; return this; }
        public BookingQuoteRequestBuilder checkOut(LocalDate checkOut) { this.checkOut = checkOut; return this; }
        public BookingQuoteRequest build() {
            return new BookingQuoteRequest(hotelId, checkIn, checkOut);
        }
    }

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }
}
