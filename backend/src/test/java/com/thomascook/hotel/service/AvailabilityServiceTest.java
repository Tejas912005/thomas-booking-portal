package com.thomascook.hotel.service;

import com.thomascook.hotel.dto.BookingResponse;
import com.thomascook.hotel.entity.Booking;
import com.thomascook.hotel.entity.BookingStatus;
import com.thomascook.hotel.entity.Hotel;
import com.thomascook.hotel.entity.RoomInventory;
import com.thomascook.hotel.exception.AlreadyCancelledException;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private RoomInventoryRepository inventoryRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = Hotel.builder()
                .id(1L)
                .name("The Taj Mahal Palace")
                .location("Mumbai")
                .description("Boutique literary hotel")
                .amenities("Free Wi-Fi, Library Bar")
                .pricePerNight(new BigDecimal("3500.00"))
                .totalRooms(2)
                .build();
    }

    private RoomInventory makeInventory(LocalDate date, int booked) {
        return RoomInventory.builder()
                .id(date.toEpochDay())
                .hotelId(hotel.getId())
                .stayDate(date)
                .totalRooms(hotel.getTotalRooms())
                .bookedRooms(booked)
                .build();
    }

    @Test
    @DisplayName("Hotel with 2 rooms, 1 booked -> 1 room available")
    void oneBooked_oneAvailable() {
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = checkIn.plusDays(2);

        List<RoomInventory> rows = List.of(
                makeInventory(checkIn, 1),
                makeInventory(checkIn.plusDays(1), 1)
        );

        when(inventoryRepository.findByHotelIdAndDateRange(eq(1L), eq(checkIn), eq(checkOut)))
                .thenReturn(rows);

        int available = availabilityService.getAvailableRooms(1L, checkIn, checkOut, 2);
        assertEquals(1, available);
        assertTrue(availabilityService.checkAvailability(1L, checkIn, checkOut, 2));
    }

    @Test
    @DisplayName("Hotel with 2 rooms, 2 booked -> 0 rooms available")
    void allBooked_zeroAvailable() {
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = checkIn.plusDays(2);

        List<RoomInventory> rows = List.of(
                makeInventory(checkIn, 2),
                makeInventory(checkIn.plusDays(1), 1)
        );

        when(inventoryRepository.findByHotelIdAndDateRange(eq(1L), eq(checkIn), eq(checkOut)))
                .thenReturn(rows);

        int available = availabilityService.getAvailableRooms(1L, checkIn, checkOut, 2);
        assertEquals(0, available);
        assertFalse(availabilityService.checkAvailability(1L, checkIn, checkOut, 2));
    }

    @Test
    @DisplayName("Check-out day is NOT counted as an occupied night")
    void checkOutDay_notCountedInStay() {
        // Stay is [day1, day3). Day 3 is fully booked, but check-out day is day 3, so stay is valid!
        LocalDate day1 = LocalDate.now().plusDays(5);
        LocalDate day2 = day1.plusDays(1);
        LocalDate day3 = day1.plusDays(2);

        // findByHotelIdAndDateRange only selects where stayDate >= day1 AND stayDate < day3
        List<RoomInventory> stayNights = List.of(
                makeInventory(day1, 1),
                makeInventory(day2, 1)
        );

        when(inventoryRepository.findByHotelIdAndDateRange(eq(1L), eq(day1), eq(day3)))
                .thenReturn(stayNights);

        int available = availabilityService.getAvailableRooms(1L, day1, day3, 2);
        assertEquals(1, available);
    }

    @Test
    @DisplayName("ensureInventoryExists lazily creates missing rows for date range")
    void ensureInventoryExists_createsMissingRows() {
        LocalDate day1 = LocalDate.now().plusDays(10);
        LocalDate day2 = day1.plusDays(1);
        LocalDate day3 = day1.plusDays(2);

        // Only day1 exists in database
        List<RoomInventory> existing = List.of(makeInventory(day1, 0));

        List<RoomInventory> result = availabilityService.ensureInventoryExists(
                hotel, day1, day3, existing);

        assertEquals(2, result.size());
        verify(inventoryRepository, times(1)).save(any(RoomInventory.class));
    }

    @Test
    @DisplayName("Cancelling a booking releases inventory and double cancel throws AlreadyCancelledException")
    void cancelBooking_releasesInventory_and_doubleCancelThrows() {
        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = checkIn.plusDays(2);

        Booking booking = Booking.builder()
                .id(50L)
                .reference("HB-ABC123")
                .hotel(hotel)
                .guestName("John Doe")
                .guestEmail("john@example.com")
                .checkIn(checkIn)
                .checkOut(checkOut)
                .nights(2)
                .totalAmount(new BigDecimal("7000.00"))
                .status(BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();

        RoomInventory row1 = makeInventory(checkIn, 1);
        RoomInventory row2 = makeInventory(checkIn.plusDays(1), 1);
        List<RoomInventory> lockedRows = List.of(row1, row2);

        when(bookingRepository.findByReferenceAndGuestEmail("HB-ABC123", "john@example.com"))
                .thenReturn(Optional.of(booking));
        when(inventoryRepository.findAndLockByHotelIdAndDateRange(eq(1L), eq(checkIn), eq(checkOut)))
                .thenReturn(lockedRows);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        BookingService bookingService = new BookingService(
                bookingRepository, hotelRepository, availabilityService, null, null);

        BookingResponse response = bookingService.cancelBooking("HB-ABC123", "john@example.com");

        assertEquals("CANCELLED", response.getStatus());
        assertEquals(0, row1.getBookedRooms());
        assertEquals(0, row2.getBookedRooms());

        // Attempting to cancel a second time should throw AlreadyCancelledException
        assertThrows(AlreadyCancelledException.class, () ->
                bookingService.cancelBooking("HB-ABC123", "john@example.com"));
    }
}
