package com.thomascook.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponse {
    private Long id;
    private String reference;
    private Long hotelId;
    private String hotelName;
    private String hotelLocation;
    private String guestName;
    private String guestEmail;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int nights;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime cancelledAt;

    public BookingResponse() {}

    public BookingResponse(Long id, String reference, Long hotelId, String hotelName, String hotelLocation,
                           String guestName, String guestEmail, LocalDate checkIn, LocalDate checkOut,
                           int nights, BigDecimal totalAmount, String status, LocalDateTime createdAt, LocalDateTime cancelledAt) {
        this.id = id;
        this.reference = reference;
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.hotelLocation = hotelLocation;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.nights = nights;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.cancelledAt = cancelledAt;
    }

    public static BookingResponseBuilder builder() {
        return new BookingResponseBuilder();
    }

    public static class BookingResponseBuilder {
        private Long id;
        private String reference;
        private Long hotelId;
        private String hotelName;
        private String hotelLocation;
        private String guestName;
        private String guestEmail;
        private LocalDate checkIn;
        private LocalDate checkOut;
        private int nights;
        private BigDecimal totalAmount;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime cancelledAt;

        public BookingResponseBuilder id(Long id) { this.id = id; return this; }
        public BookingResponseBuilder reference(String reference) { this.reference = reference; return this; }
        public BookingResponseBuilder hotelId(Long hotelId) { this.hotelId = hotelId; return this; }
        public BookingResponseBuilder hotelName(String hotelName) { this.hotelName = hotelName; return this; }
        public BookingResponseBuilder hotelLocation(String hotelLocation) { this.hotelLocation = hotelLocation; return this; }
        public BookingResponseBuilder guestName(String guestName) { this.guestName = guestName; return this; }
        public BookingResponseBuilder guestEmail(String guestEmail) { this.guestEmail = guestEmail; return this; }
        public BookingResponseBuilder checkIn(LocalDate checkIn) { this.checkIn = checkIn; return this; }
        public BookingResponseBuilder checkOut(LocalDate checkOut) { this.checkOut = checkOut; return this; }
        public BookingResponseBuilder nights(int nights) { this.nights = nights; return this; }
        public BookingResponseBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public BookingResponseBuilder status(String status) { this.status = status; return this; }
        public BookingResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public BookingResponseBuilder cancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; return this; }
        public BookingResponse build() {
            return new BookingResponse(id, reference, hotelId, hotelName, hotelLocation, guestName, guestEmail, checkIn, checkOut, nights, totalAmount, status, createdAt, cancelledAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public String getHotelLocation() { return hotelLocation; }
    public void setHotelLocation(String hotelLocation) { this.hotelLocation = hotelLocation; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }
    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }
    public int getNights() { return nights; }
    public void setNights(int nights) { this.nights = nights; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
}
