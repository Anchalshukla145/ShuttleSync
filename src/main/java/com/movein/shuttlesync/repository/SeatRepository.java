package com.movein.shuttlesync.repository;

import com.movein.shuttlesync.common.enums.SeatStatus;
import com.movein.shuttlesync.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByTripId(Long tripId);
    List<Seat> findByTripIdAndStatus(Long tripId, SeatStatus status);
    Optional<Seat> findByTripIdAndSeatNumber(Long tripId, String seatNumber);
}
