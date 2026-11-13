package com.thomascook.hotel.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings",
       indexes = {
           @Index(name = "idx_bookings_reference", columnList = "reference"),
           @Index(name = "idx_bookings_guest_email", columnList = "guest_email"),
           @Index(name = "idx_bookings_hotel_id", columnList = "hotel_id")
       })
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "guest_name", nullable = false, length = 200)
    private String guestName;

    @Column(name = "guest_email", nullable = false, length = 255)
    private String guestEmail;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(nullable = false)
    private int nights;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    public Booking() {}

    public Booking(Long id, String reference, Hotel hotel, String guestName, String guestEmail,
                   LocalDate checkIn, LocalDate checkOut, int nights, BigDecimal totalAmount,
                   BookingStatus status, LocalDateTime createdAt, LocalDateTime cancelledAt) {
        this.id = id;
        this.reference = reference;
        this.hotel = hotel;
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

    public static BookingBuilder builder() {
        return new BookingBuilder();
    }

    public static class BookingBuilder {
        private Long id;
        private String reference;
        private Hotel hotel;
        private String guestName;
        private String guestEmail;
        private LocalDate checkIn;
        private LocalDate checkOut;
        private int nights;
        private BigDecimal totalAmount;
        private BookingStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime cancelledAt;

        public BookingBuilder id(Long id) { this.id = id; return this; }
        public BookingBuilder reference(String reference) { this.reference = reference; return this; }
        public BookingBuilder hotel(Hotel hotel) { this.hotel = hotel; return this; }
        public BookingBuilder guestName(String guestName) { this.guestName = guestName; return this; }
        public BookingBuilder guestEmail(String guestEmail) { this.guestEmail = guestEmail; return this; }
        public BookingBuilder checkIn(LocalDate checkIn) { this.checkIn = checkIn; return this; }
        public BookingBuilder checkOut(LocalDate checkOut) { this.checkOut = checkOut; return this; }
        public BookingBuilder nights(int nights) { this.nights = nights; return this; }
        public BookingBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public BookingBuilder status(BookingStatus status) { this.status = status; return this; }
        public BookingBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public BookingBuilder cancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; return this; }
        public Booking build() {
            return new Booking(id, reference, hotel, guestName, guestEmail, checkIn, checkOut, nights, totalAmount, status, createdAt, cancelledAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

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

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
}
