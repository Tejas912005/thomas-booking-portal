package com.thomascook.hotel.service;

import com.thomascook.hotel.dto.BookingQuoteRequest;
import com.thomascook.hotel.dto.BookingQuoteResponse;
import com.thomascook.hotel.dto.CreateBookingRequest;
import com.thomascook.hotel.dto.BookingResponse;
import com.thomascook.hotel.entity.Booking;
import com.thomascook.hotel.entity.BookingStatus;
import com.thomascook.hotel.entity.Hotel;
import com.thomascook.hotel.entity.RoomInventory;
import com.thomascook.hotel.repository.BookingRepository;
import com.thomascook.hotel.repository.HotelRepository;
import com.thomascook.hotel.repository.RoomInventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for price calculation logic in BookingService.
 * Formula: totalAmount = nights * pricePerNight (calculated with BigDecimal)
 * where nights = ChronoUnit.DAYS.between(checkIn, checkOut)
 */
@ExtendWith(MockitoExtension.class)
class BookingPriceCalculationTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private AvailabilityService availabilityService;

    @Mock
    private NearestDatesService nearestDatesService;

    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private BookingService bookingService;

    private Hotel buildHotel(long id, BigDecimal pricePerNight, int totalRooms) {
        return Hotel.builder()
                .id(id)
                .name("Test Hotel")
                .location("Test City")
                .description("Test description")
                .amenities("Free Wi-Fi")
                .pricePerNight(pricePerNight)
                .totalRooms(totalRooms)
                .build();
    }

    // ─────────────────────────────────────────────
    // Pure math tests
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("1 night × ₹3500 = ₹3500.00")
    void oneNight_3500() {
        BigDecimal price = new BigDecimal("3500.00");
        long nights = ChronoUnit.DAYS.between(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(11));
        BigDecimal total = price.multiply(BigDecimal.valueOf(nights));
        assertEquals(new BigDecimal("3500.00"), total);
    }

    @Test
    @DisplayName("5 nights × ₹8500 = ₹42500.00")
    void fiveNights_8500() {
        BigDecimal price = new BigDecimal("8500.00");
        long nights = ChronoUnit.DAYS.between(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(15));
        BigDecimal total = price.multiply(BigDecimal.valueOf(nights));
        assertEquals(new BigDecimal("42500.00"), total);
    }

    @Test
    @DisplayName("7 nights × ₹5200 = ₹36400.00")
    void sevenNights_5200() {
        BigDecimal price = new BigDecimal("5200.00");
        long nights = ChronoUnit.DAYS.between(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(17));
        BigDecimal total = price.multiply(BigDecimal.valueOf(nights));
        assertEquals(new BigDecimal("36400.00"), total);
    }

    @Test
    @DisplayName("3 nights × ₹6800 = ₹20400.00")
    void threeNights_6800() {
        BigDecimal price = new BigDecimal("6800.00");
        long nights = ChronoUnit.DAYS.between(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(13));
        BigDecimal total = price.multiply(BigDecimal.valueOf(nights));
        assertEquals(new BigDecimal("20400.00"), total);
    }

    // ─────────────────────────────────────────────
    // BookingService.quote — computes nights and totalAmount
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("quote returns correct nights and totalAmount for 3 nights")
    void quote_computesCorrectTotal() {
        Hotel hotel = buildHotel(1L, new BigDecimal("6800.00"), 10);
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = checkIn.plusDays(3);

        BookingQuoteRequest req = BookingQuoteRequest.builder()
                .hotelId(1L)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .build();

        BookingQuoteResponse res = bookingService.quote(req);

        assertEquals(3, res.getNights());
        assertEquals(new BigDecimal("6800.00"), res.getPricePerNight());
        assertEquals(new BigDecimal("20400.00"), res.getTotalAmount());
    }

    @Test
    @DisplayName("createBooking calculates and saves correct totalAmount")
    void createBooking_savesCorrectTotalAmount() {
        Hotel hotel = buildHotel(1L, new BigDecimal("3500.00"), 5);
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = checkIn.plusDays(2);

        List<RoomInventory> lockedRows = List.of(
                RoomInventory.builder().id(1L).hotelId(1L).stayDate(checkIn).totalRooms(5).bookedRooms(0).build(),
                RoomInventory.builder().id(2L).hotelId(1L).stayDate(checkIn.plusDays(1)).totalRooms(5).bookedRooms(0).build()
        );

        when(availabilityService.lockInventoryRows(eq(1L), eq(checkIn), eq(checkOut))).thenReturn(lockedRows);
        when(availabilityService.ensureInventoryExists(eq(hotel), eq(checkIn), eq(checkOut), anyList()))
                .thenReturn(lockedRows);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(100L);
            return b;
        });

        CreateBookingRequest req = CreateBookingRequest.builder()
                .hotelId(1L)
                .guestName("Arthur Dent")
                .guestEmail("arthur@galaxy.org")
                .checkIn(checkIn)
                .checkOut(checkOut)
                .build();

        BookingResponse response = bookingService.createBooking(req);

        assertNotNull(response);
        assertEquals(2, response.getNights());
        assertEquals(new BigDecimal("7000.00"), response.getTotalAmount());
        assertTrue(response.getReference().startsWith("HB-"));
        assertEquals("CONFIRMED", response.getStatus());
    }
}
