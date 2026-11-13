package com.thomascook.hotel.repository;

import com.thomascook.hotel.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByReferenceAndGuestEmail(String reference, String guestEmail);

    Optional<Booking> findByReference(String reference);
}
