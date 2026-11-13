package com.thomascook.hotel.service;

import com.thomascook.hotel.dto.AlternativeWindow;
import com.thomascook.hotel.entity.Hotel;
import com.thomascook.hotel.entity.RoomInventory;
import com.thomascook.hotel.repository.HotelRepository;
import com.thomascook.hotel.repository.RoomInventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NearestDatesService {

    private final RoomInventoryRepository inventoryRepository;
    private final HotelRepository hotelRepository;

    public NearestDatesService(RoomInventoryRepository inventoryRepository,
                               HotelRepository hotelRepository) {
        this.inventoryRepository = inventoryRepository;
        this.hotelRepository = hotelRepository;
    }

    @Transactional(readOnly = true)
    public List<AlternativeWindow> findAlternatives(Long hotelId, LocalDate originalCheckIn,
                                                     LocalDate originalCheckOut, long nights) {
        Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
        if (hotel == null) return List.of();

        BigDecimal pricePerNight = hotel.getPricePerNight();
        LocalDate today = LocalDate.now();

        // Search window: ±14 days from original checkIn
        LocalDate windowStart = originalCheckIn.minusDays(14);
        LocalDate windowEnd = originalCheckIn.plusDays(14).plusDays(nights); // extra buffer

        // Clamp to today (don't suggest past dates)
        if (windowStart.isBefore(today)) {
            windowStart = today;
        }

        // Fetch all inventory in the search window
        List<RoomInventory> inventoryList = inventoryRepository
                .findByHotelIdAndDateRange(hotelId, windowStart, windowEnd.plusDays(nights));

        Map<LocalDate, Integer> availabilityMap = inventoryList.stream()
                .collect(Collectors.toMap(
                        RoomInventory::getStayDate,
                        ri -> ri.getTotalRooms() - ri.getBookedRooms()
                ));

        List<AlternativeWindow> results = new ArrayList<>();

        // Slide window of `nights` size through each candidate start date
        LocalDate candidateStart = windowStart;
        while (results.size() < 3 && !candidateStart.isAfter(windowEnd)) {
            // Skip the original dates
            if (candidateStart.equals(originalCheckIn)) {
                candidateStart = candidateStart.plusDays(1);
                continue;
            }

            LocalDate candidateEnd = candidateStart.plusDays(nights);

            // Check all nights in this candidate window
            boolean allAvailable = true;
            for (LocalDate d = candidateStart; d.isBefore(candidateEnd); d = d.plusDays(1)) {
                Integer available = availabilityMap.get(d);
                // If no inventory row exists, treat as available (will be created lazily)
                if (available != null && available <= 0) {
                    allAvailable = false;
                    break;
                }
            }

            if (allAvailable) {
                BigDecimal totalAmount = pricePerNight.multiply(BigDecimal.valueOf(nights));
                results.add(AlternativeWindow.builder()
                        .checkIn(candidateStart)
                        .checkOut(candidateEnd)
                        .nights(nights)
                        .totalAmount(totalAmount)
                        .build());
            }

            candidateStart = candidateStart.plusDays(1);
        }

        return results;
    }
}
