package com.thomascook.hotel.dto;

import java.math.BigDecimal;

public class BookingQuoteResponse {
    private long nights;
    private BigDecimal totalAmount;
    private BigDecimal pricePerNight;

    public BookingQuoteResponse() {}

    public BookingQuoteResponse(long nights, BigDecimal totalAmount, BigDecimal pricePerNight) {
        this.nights = nights;
        this.totalAmount = totalAmount;
        this.pricePerNight = pricePerNight;
    }

    public static BookingQuoteResponseBuilder builder() {
        return new BookingQuoteResponseBuilder();
    }

    public static class BookingQuoteResponseBuilder {
        private long nights;
        private BigDecimal totalAmount;
        private BigDecimal pricePerNight;

        public BookingQuoteResponseBuilder nights(long nights) { this.nights = nights; return this; }
        public BookingQuoteResponseBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public BookingQuoteResponseBuilder pricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; return this; }
        public BookingQuoteResponse build() {
            return new BookingQuoteResponse(nights, totalAmount, pricePerNight);
        }
    }

    public long getNights() { return nights; }
    public void setNights(long nights) { this.nights = nights; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
}
