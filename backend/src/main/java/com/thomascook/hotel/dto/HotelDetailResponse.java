package com.thomascook.hotel.dto;

import java.math.BigDecimal;
import java.util.List;

public class HotelDetailResponse {
    private Long id;
    private String name;
    private String location;
    private String description;
    private String amenities;
    private BigDecimal pricePerNight;
    private int totalRooms;
    private Integer availableRooms;
    private List<RoomAvailabilityDetail> roomAvailability;

    public HotelDetailResponse() {}

    public HotelDetailResponse(Long id, String name, String location, String description, String amenities,
                               BigDecimal pricePerNight, int totalRooms, Integer availableRooms,
                               List<RoomAvailabilityDetail> roomAvailability) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.amenities = amenities;
        this.pricePerNight = pricePerNight;
        this.totalRooms = totalRooms;
        this.availableRooms = availableRooms;
        this.roomAvailability = roomAvailability;
    }

    public static HotelDetailResponseBuilder builder() {
        return new HotelDetailResponseBuilder();
    }

    public static class HotelDetailResponseBuilder {
        private Long id;
        private String name;
        private String location;
        private String description;
        private String amenities;
        private BigDecimal pricePerNight;
        private int totalRooms;
        private Integer availableRooms;
        private List<RoomAvailabilityDetail> roomAvailability;

        public HotelDetailResponseBuilder id(Long id) { this.id = id; return this; }
        public HotelDetailResponseBuilder name(String name) { this.name = name; return this; }
        public HotelDetailResponseBuilder location(String location) { this.location = location; return this; }
        public HotelDetailResponseBuilder description(String description) { this.description = description; return this; }
        public HotelDetailResponseBuilder amenities(String amenities) { this.amenities = amenities; return this; }
        public HotelDetailResponseBuilder pricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; return this; }
        public HotelDetailResponseBuilder totalRooms(int totalRooms) { this.totalRooms = totalRooms; return this; }
        public HotelDetailResponseBuilder availableRooms(Integer availableRooms) { this.availableRooms = availableRooms; return this; }
        public HotelDetailResponseBuilder roomAvailability(List<RoomAvailabilityDetail> roomAvailability) { this.roomAvailability = roomAvailability; return this; }
        public HotelDetailResponse build() {
            return new HotelDetailResponse(id, name, location, description, amenities, pricePerNight, totalRooms, availableRooms, roomAvailability);
        }
    }

    public static class RoomAvailabilityDetail {
        private String date;
        private int available;

        public RoomAvailabilityDetail() {}
        public RoomAvailabilityDetail(String date, int available) {
            this.date = date;
            this.available = available;
        }

        public static RoomAvailabilityDetailBuilder builder() {
            return new RoomAvailabilityDetailBuilder();
        }

        public static class RoomAvailabilityDetailBuilder {
            private String date;
            private int available;
            public RoomAvailabilityDetailBuilder date(String date) { this.date = date; return this; }
            public RoomAvailabilityDetailBuilder available(int available) { this.available = available; return this; }
            public RoomAvailabilityDetail build() {
                return new RoomAvailabilityDetail(date, available);
            }
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public int getAvailable() { return available; }
        public void setAvailable(int available) { this.available = available; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
    public Integer getAvailableRooms() { return availableRooms; }
    public void setAvailableRooms(Integer availableRooms) { this.availableRooms = availableRooms; }
    public List<RoomAvailabilityDetail> getRoomAvailability() { return roomAvailability; }
    public void setRoomAvailability(List<RoomAvailabilityDetail> roomAvailability) { this.roomAvailability = roomAvailability; }
}
