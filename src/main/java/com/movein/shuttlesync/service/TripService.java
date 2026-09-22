package com.movein.shuttlesync.service;

import com.movein.shuttlesync.dto.trip.TripResponse;
import com.movein.shuttlesync.entity.Trip;
import com.movein.shuttlesync.exception.ResourceNotFoundException;
import com.movein.shuttlesync.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Transactional(readOnly = true)
    public List<TripResponse> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(this::mapToTripResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TripResponse getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", id));
        return mapToTripResponse(trip);
    }

    @Transactional(readOnly = true)
    public List<TripResponse> getTripsByRoute(Long routeId) {
        return tripRepository.findByRouteId(routeId).stream()
                .map(this::mapToTripResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TripResponse> searchTrips(Long routeId, LocalDateTime start, LocalDateTime end) {
        return tripRepository.findByRouteIdAndDepartureTimeBetween(routeId, start, end).stream()
                .map(this::mapToTripResponse)
                .collect(Collectors.toList());
    }

    private TripResponse mapToTripResponse(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getRoute().getId(),
                trip.getRoute().getName(),
                trip.getRoute().getOrigin(),
                trip.getRoute().getDestination(),
                trip.getDepartureTime(),
                trip.getArrivalTime(),
                trip.getTotalSeats(),
                trip.getAvailableSeats(),
                trip.getStatus()
        );
    }
}
