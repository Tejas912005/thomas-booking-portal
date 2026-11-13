package com.thomascook.hotel.dto;

import java.math.BigDecimal;

public class HotelSearchResponse {
    private Long id;
    private String name;
    private String location;
    private String description;
    private String amenities;
    private BigDecimal pricePerNight;
    private int totalRooms;
    private Integer availableRooms;

    public HotelSearchResponse() {}

    public HotelSearchResponse(Long id, String name, String location, String description, String amenities,
                               BigDecimal pricePerNight, int totalRooms, Integer availableRooms) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.amenities = amenities;
        this.pricePerNight = pricePerNight;
        this.totalRooms = totalRooms;
        this.availableRooms = availableRooms;
    }

    public static HotelSearchResponseBuilder builder() {
        return new HotelSearchResponseBuilder();
    }

    public static class HotelSearchResponseBuilder {
        private Long id;
        private String name;
        private String location;
        private String description;
        private String amenities;
        private BigDecimal pricePerNight;
        private int totalRooms;
        private Integer availableRooms;

        public HotelSearchResponseBuilder id(Long id) { this.id = id; return this; }
        public HotelSearchResponseBuilder name(String name) { this.name = name; return this; }
        public HotelSearchResponseBuilder location(String location) { this.location = location; return this; }
        public HotelSearchResponseBuilder description(String description) { this.description = description; return this; }
        public HotelSearchResponseBuilder amenities(String amenities) { this.amenities = amenities; return this; }
        public HotelSearchResponseBuilder pricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; return this; }
        public HotelSearchResponseBuilder totalRooms(int totalRooms) { this.totalRooms = totalRooms; return this; }
        public HotelSearchResponseBuilder availableRooms(Integer availableRooms) { this.availableRooms = availableRooms; return this; }
        public HotelSearchResponse build() {
            return new HotelSearchResponse(id, name, location, description, amenities, pricePerNight, totalRooms, availableRooms);
        }
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
}
