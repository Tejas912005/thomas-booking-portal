package com.thomascook.hotel.service;

import com.thomascook.hotel.dto.HotelDetailResponse;
import com.thomascook.hotel.dto.HotelSearchResponse;
import com.thomascook.hotel.entity.Hotel;
import com.thomascook.hotel.entity.RoomInventory;
import com.thomascook.hotel.exception.HotelNotFoundException;
import com.thomascook.hotel.repository.HotelRepository;
import com.thomascook.hotel.repository.RoomInventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomInventoryRepository inventoryRepository;
    private final AvailabilityService availabilityService;

    public HotelService(HotelRepository hotelRepository,
                        RoomInventoryRepository inventoryRepository,
                        AvailabilityService availabilityService) {
        this.hotelRepository = hotelRepository;
        this.inventoryRepository = inventoryRepository;
        this.availabilityService = availabilityService;
    }

    @Transactional(readOnly = true)
    public List<HotelSearchResponse> searchHotels(String location, LocalDate checkIn, LocalDate checkOut) {
        List<Hotel> hotels = (location != null && !location.isBlank())
                ? hotelRepository.findByLocationContainingIgnoreCase(location)
                : hotelRepository.findAll();

        List<HotelSearchResponse> responses = new ArrayList<>();

        for (Hotel hotel : hotels) {
            Integer availableRooms;

            if (checkIn != null && checkOut != null && checkIn.isBefore(checkOut)) {
                int avail = availabilityService.getAvailableRooms(
                        hotel.getId(), checkIn, checkOut, hotel.getTotalRooms());
                if (avail <= 0) {
                    // Filter out hotels with no availability
                    continue;
                }
                availableRooms = avail;
            } else {
                availableRooms = hotel.getTotalRooms();
            }

            responses.add(HotelSearchResponse.builder()
                    .id(hotel.getId())
                    .name(hotel.getName())
                    .location(hotel.getLocation())
                    .description(hotel.getDescription())
                    .amenities(hotel.getAmenities())
                    .pricePerNight(hotel.getPricePerNight())
                    .totalRooms(hotel.getTotalRooms())
                    .availableRooms(availableRooms)
                    .build());
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public HotelDetailResponse getHotelDetail(Long id, LocalDate checkIn, LocalDate checkOut) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));

        Integer availableRooms = hotel.getTotalRooms();
        List<HotelDetailResponse.RoomAvailabilityDetail> roomAvailability = new ArrayList<>();

        if (checkIn != null && checkOut != null && checkIn.isBefore(checkOut)) {
            List<RoomInventory> rows = inventoryRepository.findByHotelIdAndDateRange(
                    hotel.getId(), checkIn, checkOut);
            Map<LocalDate, Integer> availMap = rows.stream()
                    .collect(Collectors.toMap(
                            RoomInventory::getStayDate,
                            ri -> ri.getTotalRooms() - ri.getBookedRooms()
                    ));

            int minAvail = hotel.getTotalRooms();
            for (LocalDate d = checkIn; d.isBefore(checkOut); d = d.plusDays(1)) {
                int avail = availMap.getOrDefault(d, hotel.getTotalRooms());
                minAvail = Math.min(minAvail, avail);
                roomAvailability.add(HotelDetailResponse.RoomAvailabilityDetail.builder()
                        .date(d.toString())
                        .available(avail)
                        .build());
            }
            availableRooms = minAvail;
        }

        return HotelDetailResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .location(hotel.getLocation())
                .description(hotel.getDescription())
                .amenities(hotel.getAmenities())
                .pricePerNight(hotel.getPricePerNight())
                .totalRooms(hotel.getTotalRooms())
                .availableRooms(availableRooms)
                .roomAvailability(roomAvailability.isEmpty() ? null : roomAvailability)
                .build();
    }
}
