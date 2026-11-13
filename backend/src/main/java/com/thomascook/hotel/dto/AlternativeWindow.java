package com.thomascook.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AlternativeWindow {
    private LocalDate checkIn;
    private LocalDate checkOut;
    private long nights;
    private BigDecimal totalAmount;

    public AlternativeWindow() {}

    public AlternativeWindow(LocalDate checkIn, LocalDate checkOut, long nights, BigDecimal totalAmount) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.nights = nights;
        this.totalAmount = totalAmount;
    }

    public static AlternativeWindowBuilder builder() {
        return new AlternativeWindowBuilder();
    }

    public static class AlternativeWindowBuilder {
        private LocalDate checkIn;
        private LocalDate checkOut;
        private long nights;
        private BigDecimal totalAmount;

        public AlternativeWindowBuilder checkIn(LocalDate checkIn) { this.checkIn = checkIn; return this; }
        public AlternativeWindowBuilder checkOut(LocalDate checkOut) { this.checkOut = checkOut; return this; }
        public AlternativeWindowBuilder nights(long nights) { this.nights = nights; return this; }
        public AlternativeWindowBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public AlternativeWindow build() {
            return new AlternativeWindow(checkIn, checkOut, nights, totalAmount);
        }
    }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }
    public long getNights() { return nights; }
    public void setNights(long nights) { this.nights = nights; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
