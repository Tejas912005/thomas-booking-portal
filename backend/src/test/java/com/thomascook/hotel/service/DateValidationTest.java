package com.thomascook.hotel.service;

import com.thomascook.hotel.dto.BookingQuoteRequest;
import com.thomascook.hotel.dto.CreateBookingRequest;
import com.thomascook.hotel.entity.Hotel;
import com.thomascook.hotel.exception.InvalidBookingDatesException;
import com.thomascook.hotel.repository.BookingRepository;
import com.thomascook.hotel.repository.HotelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for date validation rules enforced by BookingService:
 *  1. checkIn in the past -> InvalidBookingDatesException
 *  2. checkOut == checkIn -> InvalidBookingDatesException (must be strictly after)
 *  3. checkOut before checkIn -> InvalidBookingDatesException
 *  4. Valid future dates -> passes date validation
 */
@ExtendWith(MockitoExtension.class)
class DateValidationTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AvailabilityService availabilityService;

    @Mock
    private NearestDatesService nearestDatesService;

    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private BookingService bookingService;

    @Test
    @DisplayName("checkIn in the past throws InvalidBookingDatesException")
    void checkIn_inPast_throwsException() {
        Hotel hotel = Hotel.builder()
                .id(1L)
                .name("Hotel Test")
                .pricePerNight(new BigDecimal("4000.00"))
                .totalRooms(5)
                .build();
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        BookingQuoteRequest req = BookingQuoteRequest.builder()
                .hotelId(1L)
                .checkIn(LocalDate.now().minusDays(1))
                .checkOut(LocalDate.now().plusDays(2))
                .build();

        InvalidBookingDatesException ex = assertThrows(
                InvalidBookingDatesException.class,
                () -> bookingService.quote(req)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("past"));
    }

    @Test
    @DisplayName("checkOut equals checkIn throws InvalidBookingDatesException")
    void checkOut_equalsCheckIn_throwsException() {
        Hotel hotel = Hotel.builder()
                .id(1L)
                .name("Hotel Test")
                .pricePerNight(new BigDecimal("4000.00"))
                .totalRooms(5)
                .build();
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        LocalDate date = LocalDate.now().plusDays(5);
        BookingQuoteRequest req = BookingQuoteRequest.builder()
                .hotelId(1L)
                .checkIn(date)
                .checkOut(date)
                .build();

        InvalidBookingDatesException ex = assertThrows(
                InvalidBookingDatesException.class,
                () -> bookingService.quote(req)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("after"));
    }

    @Test
    @DisplayName("checkOut before checkIn throws InvalidBookingDatesException")
    void checkOut_beforeCheckIn_throwsException() {
        Hotel hotel = Hotel.builder()
                .id(1L)
                .name("Hotel Test")
                .pricePerNight(new BigDecimal("4000.00"))
                .totalRooms(5)
                .build();
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        BookingQuoteRequest req = BookingQuoteRequest.builder()
                .hotelId(1L)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .build();

        InvalidBookingDatesException ex = assertThrows(
                InvalidBookingDatesException.class,
                () -> bookingService.quote(req)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("after"));
    }

    @Test
    @DisplayName("Valid future dates pass date validation without throwing")
    void validDates_passValidation() {
        Hotel hotel = Hotel.builder()
                .id(1L)
                .name("Hotel Test")
                .pricePerNight(new BigDecimal("4000.00"))
                .totalRooms(5)
                .build();
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = checkIn.plusDays(3);

        BookingQuoteRequest req = BookingQuoteRequest.builder()
                .hotelId(1L)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .build();

        assertDoesNotThrow(() -> bookingService.quote(req));
    }
}
