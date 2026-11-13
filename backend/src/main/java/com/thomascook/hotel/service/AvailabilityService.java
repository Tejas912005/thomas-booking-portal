package com.thomascook.hotel.service;

import com.thomascook.hotel.entity.Hotel;
import com.thomascook.hotel.entity.RoomInventory;
import com.thomascook.hotel.repository.RoomInventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    private final RoomInventoryRepository inventoryRepository;

    public AvailabilityService(RoomInventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Returns the minimum available rooms across all nights in [checkIn, checkOut).
     * If no inventory exists for a date, it's treated as hotel's totalRooms.
     */
    @Transactional(readOnly = true)
    public int getAvailableRooms(Long hotelId, LocalDate checkIn, LocalDate checkOut, int hotelTotalRooms) {
        List<RoomInventory> rows = inventoryRepository.findByHotelIdAndDateRange(hotelId, checkIn, checkOut);
        Map<LocalDate, Integer> available = rows.stream()
                .collect(Collectors.toMap(
                        RoomInventory::getStayDate,
                        ri -> ri.getTotalRooms() - ri.getBookedRooms()
                ));

        int minAvailable = hotelTotalRooms;
        for (LocalDate d = checkIn; d.isBefore(checkOut); d = d.plusDays(1)) {
            int avail = available.getOrDefault(d, hotelTotalRooms);
            minAvailable = Math.min(minAvailable, avail);
        }
        return minAvailable;
    }

    /**
     * Checks if at least 1 room is available on every night in [checkIn, checkOut).
     */
    @Transactional(readOnly = true)
    public boolean checkAvailability(Long hotelId, LocalDate checkIn, LocalDate checkOut, int hotelTotalRooms) {
        return getAvailableRooms(hotelId, checkIn, checkOut, hotelTotalRooms) > 0;
    }

    /**
     * Locks and returns all inventory rows for [checkIn, checkOut).
     * Uses PESSIMISTIC_WRITE for concurrency safety.
     */
    @Transactional
    public List<RoomInventory> lockInventoryRows(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        return inventoryRepository.findAndLockByHotelIdAndDateRange(hotelId, checkIn, checkOut);
    }

    /**
     * Lazily creates missing inventory rows for the given date range.
     * Assumes it runs within an existing transaction with locked rows.
     */
    @Transactional
    public List<RoomInventory> ensureInventoryExists(Hotel hotel, LocalDate checkIn,
                                                      LocalDate checkOut,
                                                      List<RoomInventory> existingRows) {
        Map<LocalDate, RoomInventory> existingMap = existingRows.stream()
                .collect(Collectors.toMap(RoomInventory::getStayDate, r -> r));

        List<RoomInventory> allRows = new ArrayList<>(existingRows);
        for (LocalDate d = checkIn; d.isBefore(checkOut); d = d.plusDays(1)) {
            if (!existingMap.containsKey(d)) {
                RoomInventory newRow = RoomInventory.builder()
                        .hotelId(hotel.getId())
                        .stayDate(d)
                        .totalRooms(hotel.getTotalRooms())
                        .bookedRooms(0)
                        .build();
                inventoryRepository.save(newRow);
                allRows.add(newRow);
            }
        }
        // Sort by date to maintain order
        allRows.sort((a, b) -> a.getStayDate().compareTo(b.getStayDate()));
        return allRows;
    }
}
