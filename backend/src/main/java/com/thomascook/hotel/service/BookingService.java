package com.thomascook.hotel.service;

import com.thomascook.hotel.dto.*;
import com.thomascook.hotel.entity.Booking;
import com.thomascook.hotel.entity.BookingStatus;
import com.thomascook.hotel.entity.Hotel;
import com.thomascook.hotel.entity.RoomInventory;
import com.thomascook.hotel.exception.*;
import com.thomascook.hotel.repository.BookingRepository;
import com.thomascook.hotel.repository.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final AvailabilityService availabilityService;
    private final NearestDatesService nearestDatesService;
    private final EmailNotificationService emailNotificationService;

    public BookingService(BookingRepository bookingRepository,
                          HotelRepository hotelRepository,
                          AvailabilityService availabilityService,
                          NearestDatesService nearestDatesService,
                          EmailNotificationService emailNotificationService) {
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.availabilityService = availabilityService;
        this.nearestDatesService = nearestDatesService;
        this.emailNotificationService = emailNotificationService;
    }

    @Transactional(readOnly = true)
    public BookingQuoteResponse quote(BookingQuoteRequest req) {
        Hotel hotel = hotelRepository.findById(req.getHotelId())
                .orElseThrow(() -> new HotelNotFoundException(req.getHotelId()));

        validateDates(req.getCheckIn(), req.getCheckOut());

        long nights = ChronoUnit.DAYS.between(req.getCheckIn(), req.getCheckOut());
        BigDecimal totalAmount = hotel.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        return BookingQuoteResponse.builder()
                .nights(nights)
                .pricePerNight(hotel.getPricePerNight())
                .totalAmount(totalAmount)
                .build();
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest req) {
        // 1. Validate dates
        validateDates(req.getCheckIn(), req.getCheckOut());

        // 2. Find hotel
        Hotel hotel = hotelRepository.findById(req.getHotelId())
                .orElseThrow(() -> new HotelNotFoundException(req.getHotelId()));

        // 3. Calculate nights
        long nights = ChronoUnit.DAYS.between(req.getCheckIn(), req.getCheckOut());

        // 4. Lock inventory rows with PESSIMISTIC_WRITE in stay_date ORDER (deadlock avoidance)
        List<RoomInventory> lockedRows = availabilityService.lockInventoryRows(
                hotel.getId(), req.getCheckIn(), req.getCheckOut());

        // 5. If any rows are missing for the range, create them lazily
        lockedRows = availabilityService.ensureInventoryExists(
                hotel, req.getCheckIn(), req.getCheckOut(), lockedRows);

        // 6. Re-check availability after locking
        for (RoomInventory row : lockedRows) {
            if (row.getBookedRooms() >= row.getTotalRooms()) {
                List<AlternativeWindow> alternatives = nearestDatesService.findAlternatives(
                        hotel.getId(), req.getCheckIn(), req.getCheckOut(), nights);
                throw new NoAvailabilityException(alternatives);
            }
        }

        // 7. Increment bookedRooms on each row
        for (RoomInventory row : lockedRows) {
            row.setBookedRooms(row.getBookedRooms() + 1);
        }

        // 8. Calculate total amount
        BigDecimal totalAmount = hotel.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        // 9. Generate reference
        String reference = generateReference();

        // 10. Save booking
        Booking booking = Booking.builder()
                .reference(reference)
                .hotel(hotel)
                .guestName(req.getGuestName())
                .guestEmail(req.getGuestEmail())
                .checkIn(req.getCheckIn())
                .checkOut(req.getCheckOut())
                .nights((int) nights)
                .totalAmount(totalAmount)
                .status(BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .cancelledAt(null)
                .build();

        booking = bookingRepository.save(booking);
        
        // Dispatch confirmation email if notification service is configured
        if (emailNotificationService != null) {
            emailNotificationService.sendBookingConfirmation(booking);
        }

        return toResponse(booking);
    }

    @Transactional(readOnly = true)
    public BookingResponse lookupBooking(String reference, String email) {
        Booking booking = bookingRepository.findByReferenceAndGuestEmail(reference, email)
                .orElseThrow(BookingNotFoundException::new);
        return toResponse(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(String reference, String email) {
        Booking booking = bookingRepository.findByReferenceAndGuestEmail(reference, email)
                .orElseThrow(BookingNotFoundException::new);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new AlreadyCancelledException();
        }

        // Lock inventory rows in date order (deadlock avoidance)
        List<RoomInventory> lockedRows = availabilityService.lockInventoryRows(
                booking.getHotel().getId(), booking.getCheckIn(), booking.getCheckOut());

        // Decrement bookedRooms on each row
        for (RoomInventory row : lockedRows) {
            if (row.getBookedRooms() > 0) {
                row.setBookedRooms(row.getBookedRooms() - 1);
            }
        }

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());

        booking = bookingRepository.save(booking);
        
        // Dispatch confirmation email if notification service is configured
        if (emailNotificationService != null) {
            emailNotificationService.sendBookingConfirmation(booking);
        }

        return toResponse(booking);
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        LocalDate today = LocalDate.now();
        if (checkIn.isBefore(today)) {
            throw new InvalidBookingDatesException("Check-in date cannot be in the past");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new InvalidBookingDatesException("Check-out date must be after check-in date");
        }
    }

    private String generateReference() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder("HB-");
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .reference(booking.getReference())
                .hotelId(booking.getHotel().getId())
                .hotelName(booking.getHotel().getName())
                .hotelLocation(booking.getHotel().getLocation())
                .guestName(booking.getGuestName())
                .guestEmail(booking.getGuestEmail())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .nights(booking.getNights())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus().name())
                .createdAt(booking.getCreatedAt())
                .cancelledAt(booking.getCancelledAt())
                .build();
    }
}
