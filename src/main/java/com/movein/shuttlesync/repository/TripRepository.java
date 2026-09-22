package com.movein.shuttlesync.repository;

import com.movein.shuttlesync.common.enums.TripStatus;
import com.movein.shuttlesync.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByRouteId(Long routeId);
    List<Trip> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Trip> findByRouteIdAndDepartureTimeBetween(Long routeId, LocalDateTime start, LocalDateTime end);
    List<Trip> findByStatus(TripStatus status);
}
