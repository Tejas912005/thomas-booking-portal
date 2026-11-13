package com.thomascook.hotel.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "room_inventory",
       uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_id", "stay_date"}))
public class RoomInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "stay_date", nullable = false)
    private LocalDate stayDate;

    @Column(name = "total_rooms", nullable = false)
    private int totalRooms;

    @Column(name = "booked_rooms", nullable = false)
    private int bookedRooms;

    public RoomInventory() {}

    public RoomInventory(Long id, Long hotelId, LocalDate stayDate, int totalRooms, int bookedRooms) {
        this.id = id;
        this.hotelId = hotelId;
        this.stayDate = stayDate;
        this.totalRooms = totalRooms;
        this.bookedRooms = bookedRooms;
    }

    public static RoomInventoryBuilder builder() {
        return new RoomInventoryBuilder();
    }

    public static class RoomInventoryBuilder {
        private Long id;
        private Long hotelId;
        private LocalDate stayDate;
        private int totalRooms;
        private int bookedRooms;

        public RoomInventoryBuilder id(Long id) { this.id = id; return this; }
        public RoomInventoryBuilder hotelId(Long hotelId) { this.hotelId = hotelId; return this; }
        public RoomInventoryBuilder stayDate(LocalDate stayDate) { this.stayDate = stayDate; return this; }
        public RoomInventoryBuilder totalRooms(int totalRooms) { this.totalRooms = totalRooms; return this; }
        public RoomInventoryBuilder bookedRooms(int bookedRooms) { this.bookedRooms = bookedRooms; return this; }
        public RoomInventory build() {
            return new RoomInventory(id, hotelId, stayDate, totalRooms, bookedRooms);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public LocalDate getStayDate() { return stayDate; }
    public void setStayDate(LocalDate stayDate) { this.stayDate = stayDate; }

    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }

    public int getBookedRooms() { return bookedRooms; }
    public void setBookedRooms(int bookedRooms) { this.bookedRooms = bookedRooms; }
}
