package com.movein.shuttlesync.service;

import com.movein.shuttlesync.common.enums.SeatStatus;
import com.movein.shuttlesync.entity.Seat;
import com.movein.shuttlesync.entity.Trip;
import com.movein.shuttlesync.exception.BookingException;
import com.movein.shuttlesync.repository.SeatRepository;
import com.movein.shuttlesync.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatAllocationService {

    private final SeatRepository seatRepository;
    private final TripRepository tripRepository;

    public SeatAllocationService(SeatRepository seatRepository, TripRepository tripRepository) {
        this.seatRepository = seatRepository;
        this.tripRepository = tripRepository;
    }

    @Transactional
    public Seat allocateSeat(Trip trip, Long requestedSeatId) {
        if (requestedSeatId != null) {
            Seat seat = seatRepository.findById(requestedSeatId)
                    .orElseThrow(() -> new BookingException("Seat not found with id: " + requestedSeatId));
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new BookingException("Seat " + seat.getSeatNumber() + " is not available");
            }
            seat.setStatus(SeatStatus.RESERVED);
            return seatRepository.save(seat);
        }

        List<Seat> availableSeats = seatRepository.findByTripIdAndStatus(trip.getId(), SeatStatus.AVAILABLE);
        if (availableSeats.isEmpty()) {
            throw new BookingException("No seats available for trip id: " + trip.getId());
        }

        Seat seat = availableSeats.get(0);
        seat.setStatus(SeatStatus.RESERVED);
        return seatRepository.save(seat);
    }

    @Transactional
    public void releaseSeat(Seat seat) {
        if (seat != null) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepository.save(seat);
        }
    }
}
