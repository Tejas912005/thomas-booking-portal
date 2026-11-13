package com.thomascook.hotel.concurrency;

import com.thomascook.hotel.dto.CreateBookingRequest;
import com.thomascook.hotel.entity.RoomInventory;
import com.thomascook.hotel.repository.BookingRepository;
import com.thomascook.hotel.repository.RoomInventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Concurrency integration test:
 * When two threads simultaneously attempt to book the LAST available room,
 * exactly one succeeds (HTTP 200) and the other fails with HTTP 409 (No Availability).
 *
 * Uses PESSIMISTIC_WRITE locks ordered by stay_date to eliminate race conditions.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LastRoomConcurrencyIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomInventoryRepository roomInventoryRepository;

    private static final long HOTEL_ID = 1L;
    private static final int TOTAL_ROOMS = 2;

    private LocalDate checkIn;
    private LocalDate checkOut;

    @BeforeEach
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void setUpContestedInventory() {
        checkIn = LocalDate.now().plusDays(45);
        checkOut = checkIn.plusDays(1);

        List<RoomInventory> rows = roomInventoryRepository.findByHotelIdAndDateRange(
                HOTEL_ID, checkIn, checkOut);

        if (rows.isEmpty()) {
            RoomInventory inv = RoomInventory.builder()
                    .hotelId(HOTEL_ID)
                    .stayDate(checkIn)
                    .totalRooms(TOTAL_ROOMS)
                    .bookedRooms(TOTAL_ROOMS - 1) // Exactly 1 room left
                    .build();
            roomInventoryRepository.save(inv);
        } else {
            for (RoomInventory r : rows) {
                r.setTotalRooms(TOTAL_ROOMS);
                r.setBookedRooms(TOTAL_ROOMS - 1); // Exactly 1 room left
            }
            roomInventoryRepository.saveAll(rows);
        }

        // Clean up prior bookings on that date
        bookingRepository.findAll().stream()
                .filter(b -> b.getHotel() != null
                        && b.getHotel().getId().equals(HOTEL_ID)
                        && !b.getCheckIn().isAfter(checkIn)
                        && b.getCheckOut().isAfter(checkIn))
                .forEach(bookingRepository::delete);
    }

    @Test
    @DisplayName("Two simultaneous booking requests for the last room: exactly one succeeds and one gets 409")
    void whenTwoUsersBookLastRoom_exactlyOneSucceeds() throws Exception {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        for (int i = 0; i < 2; i++) {
            final int guestNum = i + 1;
            executor.submit(() -> {
                try {
                    startLatch.await(); // Synchronised start

                    CreateBookingRequest request = CreateBookingRequest.builder()
                            .hotelId(HOTEL_ID)
                            .guestName("Racer Guest " + guestNum)
                            .guestEmail("racer" + guestNum + "@grandtour.com")
                            .checkIn(checkIn)
                            .checkOut(checkOut)
                            .build();

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<CreateBookingRequest> entity = new HttpEntity<>(request, headers);

                    ResponseEntity<String> response = restTemplate.postForEntity(
                            "/api/bookings", entity, String.class);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        successCount.incrementAndGet();
                    } else if (response.getStatusCode().value() == 409) {
                        conflictCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    // Ignore interrupted exceptions
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Fire both threads simultaneously
        boolean completed = doneLatch.await(15, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertTrue(completed, "Both requests should finish within timeout");
        assertEquals(1, successCount.get(), "Exactly one booking should succeed");
        assertEquals(1, conflictCount.get(), "Exactly one booking should fail with 409 Conflict");
    }
}
