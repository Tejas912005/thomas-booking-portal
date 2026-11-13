package com.thomascook.hotel.repository;

import com.thomascook.hotel.entity.RoomInventory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomInventoryRepository extends JpaRepository<RoomInventory, Long> {

    List<RoomInventory> findByHotelIdAndStayDateBetweenOrderByStayDate(
            Long hotelId, LocalDate startDate, LocalDate endDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000"))
    @Query("SELECT r FROM RoomInventory r WHERE r.hotelId = :hotelId AND r.stayDate >= :checkIn AND r.stayDate < :checkOut ORDER BY r.stayDate")
    List<RoomInventory> findAndLockByHotelIdAndDateRange(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);

    @Query("SELECT r FROM RoomInventory r WHERE r.hotelId = :hotelId AND r.stayDate >= :checkIn AND r.stayDate < :checkOut ORDER BY r.stayDate")
    List<RoomInventory> findByHotelIdAndDateRange(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);
}
