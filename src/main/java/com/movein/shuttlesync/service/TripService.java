package com.movein.shuttlesync.service;

import com.movein.shuttlesync.common.enums.SeatStatus;
import com.movein.shuttlesync.common.enums.TripStatus;
import com.movein.shuttlesync.dto.trip.TripRequest;
import com.movein.shuttlesync.dto.trip.TripResponse;
import com.movein.shuttlesync.entity.Route;
import com.movein.shuttlesync.entity.Seat;
import com.movein.shuttlesync.entity.Trip;
import com.movein.shuttlesync.exception.ResourceNotFoundException;
import com.movein.shuttlesync.repository.RouteRepository;
import com.movein.shuttlesync.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;

    public TripService(TripRepository tripRepository, RouteRepository routeRepository) {
        this.tripRepository = tripRepository;
        this.routeRepository = routeRepository;
    }

    @Transactional
    public TripResponse createTrip(TripRequest request) {
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.getRouteId()));

        if (request.getDepartureTime().isAfter(request.getArrivalTime()) || request.getDepartureTime().isEqual(request.getArrivalTime())) {
            throw new IllegalArgumentException("Departure time must be before arrival time");
        }

        if (request.getTotalSeats() == null || request.getTotalSeats() <= 0) {
            throw new IllegalArgumentException("Total seats must be positive");
        }

        Trip trip = new Trip();
        trip.setRoute(route);
        trip.setDepartureTime(request.getDepartureTime());
        trip.setArrivalTime(request.getArrivalTime());
        trip.setTotalSeats(request.getTotalSeats());
        trip.setAvailableSeats(request.getTotalSeats());
        trip.setStatus(TripStatus.SCHEDULED);

        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= request.getTotalSeats(); i++) {
            Seat seat = new Seat();
            seat.setSeatNumber("S" + i);
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setTrip(trip);
            seats.add(seat);
        }
        trip.setSeats(seats);

        Trip savedTrip = tripRepository.save(trip);
        return mapToTripResponse(savedTrip);
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
