package com.movein.shuttlesync.service;

import com.movein.shuttlesync.entity.Seat;
import com.movein.shuttlesync.entity.Stop;
import com.movein.shuttlesync.entity.Trip;
import com.movein.shuttlesync.exception.BookingException;
import com.movein.shuttlesync.exception.ResourceNotFoundException;
import com.movein.shuttlesync.repository.SeatRepository;
import com.movein.shuttlesync.repository.StopRepository;
import com.movein.shuttlesync.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AvailabilityService {

    private final TripRepository tripRepository;
    private final SeatRepository seatRepository;
    private final StopRepository stopRepository;
    private final SeatAllocationService seatAllocationService;

    public AvailabilityService(TripRepository tripRepository,
                               SeatRepository seatRepository,
                               StopRepository stopRepository,
                               SeatAllocationService seatAllocationService) {
        this.tripRepository = tripRepository;
        this.seatRepository = seatRepository;
        this.stopRepository = stopRepository;
        this.seatAllocationService = seatAllocationService;
    }

    /**
     * Checks if a trip has at least one seat available for a given route segment.
     */
    @Transactional(readOnly = true)
    public boolean isTripAvailableForSegment(Long tripId, Long pickupStopId, Long dropoffStopId) {
        return !getAvailableSeatsForSegment(tripId, pickupStopId, dropoffStopId).isEmpty();
    }

    /**
     * Returns all seats available for the specified segment on a trip.
     */
    @Transactional(readOnly = true)
    public List<Seat> getAvailableSeatsForSegment(Long tripId, Long pickupStopId, Long dropoffStopId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));

        Stop pickupStop = stopRepository.findById(pickupStopId)
                .orElseThrow(() -> new ResourceNotFoundException("Stop", "id", pickupStopId));

        Stop dropoffStop = stopRepository.findById(dropoffStopId)
                .orElseThrow(() -> new ResourceNotFoundException("Stop", "id", dropoffStopId));

        if (pickupStop.getSequenceOrder() >= dropoffStop.getSequenceOrder()) {
            throw new BookingException("Pickup stop must precede dropoff stop in the route sequence");
        }

        int startSeq = pickupStop.getSequenceOrder();
        int endSeq = dropoffStop.getSequenceOrder();

        List<Seat> allSeats = seatRepository.findByTripIdOrderByIdAsc(trip.getId());
        List<Seat> availableSeats = new ArrayList<>();

        for (Seat seat : allSeats) {
            if (seatAllocationService.isSeatAvailableForSegment(seat, startSeq, endSeq)) {
                availableSeats.add(seat);
            }
        }

        return availableSeats;
    }

    /**
     * Legacy helper to check if trip has any general seat configured.
     */
    @Transactional(readOnly = true)
    public boolean isTripAvailable(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));
        return trip.getSeats() != null && !trip.getSeats().isEmpty();
    }

    /**
     * Legacy helper to return all seats configured for a trip.
     */
    @Transactional(readOnly = true)
    public List<Seat> getAvailableSeats(Long tripId) {
        return seatRepository.findByTripId(tripId);
    }
}
