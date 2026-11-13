package com.thomascook.hotel.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class CreateBookingRequest {

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotBlank(message = "Guest name is required")
    private String guestName;

    @NotBlank(message = "Guest email is required")
    @Email(message = "Guest email must be a valid email address")
    private String guestEmail;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOut;

    public CreateBookingRequest() {}

    public CreateBookingRequest(Long hotelId, String guestName, String guestEmail, LocalDate checkIn, LocalDate checkOut) {
        this.hotelId = hotelId;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public static CreateBookingRequestBuilder builder() {
        return new CreateBookingRequestBuilder();
    }

    public static class CreateBookingRequestBuilder {
        private Long hotelId;
        private String guestName;
        private String guestEmail;
        private LocalDate checkIn;
        private LocalDate checkOut;

        public CreateBookingRequestBuilder hotelId(Long hotelId) { this.hotelId = hotelId; return this; }
        public CreateBookingRequestBuilder guestName(String guestName) { this.guestName = guestName; return this; }
        public CreateBookingRequestBuilder guestEmail(String guestEmail) { this.guestEmail = guestEmail; return this; }
        public CreateBookingRequestBuilder checkIn(LocalDate checkIn) { this.checkIn = checkIn; return this; }
        public CreateBookingRequestBuilder checkOut(LocalDate checkOut) { this.checkOut = checkOut; return this; }
        public CreateBookingRequest build() {
            return new CreateBookingRequest(hotelId, guestName, guestEmail, checkIn, checkOut);
        }
    }

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }
    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }
}
