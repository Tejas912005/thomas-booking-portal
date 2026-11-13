package com.thomascook.hotel.service;

import com.thomascook.hotel.dto.AlternativeWindow;
import com.thomascook.hotel.entity.Hotel;
import com.thomascook.hotel.entity.RoomInventory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NearestDatesService:
 *  1. Suggests up to 3 alternative windows with same number of nights within +/-14 days
 *  2. Alternatives have computed total amount
 *  3. Alternatives exclude original dates
 *  4. No alternative in the past
 */
@ExtendWith(MockitoExtension.class)
class NearestDatesServiceTest {

    @Mock
    private RoomInventoryRepository inventoryRepository;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private NearestDatesService nearestDatesService;

    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = Hotel.builder()
                .id(1L)
                .name("The Taj Mahal Palace")
                .location("Mumbai")
                .description("Boutique literary hotel")
                .amenities("Free Wi-Fi")
                .pricePerNight(new BigDecimal("3500.00"))
                .totalRooms(2)
                .build();
    }

    @Test
    @DisplayName("Finds up to 3 available alternative windows when original dates are full")
    void findAlternatives_returnsUpTo3Windows() {
        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = checkIn.plusDays(3);
        long nights = 3;

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        // Original dates are full (bookedRooms = 2)
        List<RoomInventory> inventory = new ArrayList<>();
        inventory.add(RoomInventory.builder().hotelId(1L).stayDate(checkIn).totalRooms(2).bookedRooms(2).build());
        inventory.add(RoomInventory.builder().hotelId(1L).stayDate(checkIn.plusDays(1)).totalRooms(2).bookedRooms(2).build());
        inventory.add(RoomInventory.builder().hotelId(1L).stayDate(checkIn.plusDays(2)).totalRooms(2).bookedRooms(2).build());

        when(inventoryRepository.findByHotelIdAndDateRange(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(inventory);

        List<AlternativeWindow> alternatives = nearestDatesService.findAlternatives(
                1L, checkIn, checkOut, nights);

        assertNotNull(alternatives);
        assertTrue(alternatives.size() <= 3);
        for (AlternativeWindow alt : alternatives) {
            assertEquals(3, alt.getNights());
            assertEquals(new BigDecimal("10500.00"), alt.getTotalAmount());
            assertNotEquals(checkIn, alt.getCheckIn(), "Alternative should not be original check-in");
            assertFalse(alt.getCheckIn().isBefore(LocalDate.now()), "Alternative cannot be in past");
        }
    }

    @Test
    @DisplayName("Returns empty list if hotel does not exist")
    void findAlternatives_hotelNotFound_returnsEmpty() {
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

        List<AlternativeWindow> res = nearestDatesService.findAlternatives(
                999L, LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), 2);

        assertTrue(res.isEmpty());
    }
}
