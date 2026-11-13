package com.thomascook.hotel.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 120)
    private String location;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String amenities;

    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Column(name = "total_rooms", nullable = false)
    private int totalRooms;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Hotel() {}

    public Hotel(Long id, String name, String location, String description, String amenities, BigDecimal pricePerNight, int totalRooms, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.amenities = amenities;
        this.pricePerNight = pricePerNight;
        this.totalRooms = totalRooms;
        this.createdAt = createdAt;
    }

    public static HotelBuilder builder() {
        return new HotelBuilder();
    }

    public static class HotelBuilder {
        private Long id;
        private String name;
        private String location;
        private String description;
        private String amenities;
        private BigDecimal pricePerNight;
        private int totalRooms;
        private LocalDateTime createdAt;

        public HotelBuilder id(Long id) { this.id = id; return this; }
        public HotelBuilder name(String name) { this.name = name; return this; }
        public HotelBuilder location(String location) { this.location = location; return this; }
        public HotelBuilder description(String description) { this.description = description; return this; }
        public HotelBuilder amenities(String amenities) { this.amenities = amenities; return this; }
        public HotelBuilder pricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; return this; }
        public HotelBuilder totalRooms(int totalRooms) { this.totalRooms = totalRooms; return this; }
        public HotelBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Hotel build() {
            return new Hotel(id, name, location, description, amenities, pricePerNight, totalRooms, createdAt);
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
