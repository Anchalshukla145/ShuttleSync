package com.movein.shuttlesync.service;

import com.movein.shuttlesync.common.enums.SeatStatus;
import com.movein.shuttlesync.entity.Seat;
import com.movein.shuttlesync.entity.Trip;
import com.movein.shuttlesync.exception.ResourceNotFoundException;
import com.movein.shuttlesync.repository.SeatRepository;
import com.movein.shuttlesync.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvailabilityService {

    private final TripRepository tripRepository;
    private final SeatRepository seatRepository;

    public AvailabilityService(TripRepository tripRepository, SeatRepository seatRepository) {
        this.tripRepository = tripRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional(readOnly = true)
    public boolean isTripAvailable(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));
        return trip.getAvailableSeats() != null && trip.getAvailableSeats() > 0;
    }

    @Transactional(readOnly = true)
    public List<Seat> getAvailableSeats(Long tripId) {
        return seatRepository.findByTripIdAndStatus(tripId, SeatStatus.AVAILABLE);
    }
}
