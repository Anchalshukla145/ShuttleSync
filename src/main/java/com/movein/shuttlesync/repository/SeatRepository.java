package com.movein.shuttlesync.repository;

import com.movein.shuttlesync.common.enums.SeatStatus;
import com.movein.shuttlesync.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByTripId(Long tripId);
    List<Seat> findByTripIdOrderByIdAsc(Long tripId);
    List<Seat> findByTripIdAndStatus(Long tripId, SeatStatus status);
    Optional<Seat> findByTripIdAndSeatNumber(Long tripId, String seatNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.trip.id = :tripId ORDER BY s.id ASC")
    List<Seat> findByTripIdOrderByIdAscForUpdate(@Param("tripId") Long tripId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :id")
    Optional<Seat> findByIdForUpdate(@Param("id") Long id);
}
