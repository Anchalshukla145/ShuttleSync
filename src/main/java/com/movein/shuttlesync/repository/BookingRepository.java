package com.movein.shuttlesync.repository;

import com.movein.shuttlesync.common.enums.BookingStatus;
import com.movein.shuttlesync.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByTripId(Long tripId);
    List<Booking> findByTripIdAndStatus(Long tripId, BookingStatus status);
}
