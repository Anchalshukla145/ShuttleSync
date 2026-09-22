package com.movein.shuttlesync.service;

import com.movein.shuttlesync.common.enums.BookingStatus;
import com.movein.shuttlesync.entity.Booking;
import com.movein.shuttlesync.entity.Seat;
import com.movein.shuttlesync.entity.Stop;
import com.movein.shuttlesync.entity.Trip;
import com.movein.shuttlesync.exception.BookingException;
import com.movein.shuttlesync.exception.ResourceNotFoundException;
import com.movein.shuttlesync.repository.BookingRepository;
import com.movein.shuttlesync.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatAllocationService {

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    public SeatAllocationService(SeatRepository seatRepository, BookingRepository bookingRepository) {
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Allocates a seat for the given trip and segment.
     * Uses pessimistic locking to prevent race conditions during concurrent booking requests.
     */
    @Transactional
    public Seat allocateSeat(Trip trip, Stop pickupStop, Stop dropoffStop, Long requestedSeatId) {
        int startSequence = pickupStop.getSequenceOrder();
        int endSequence = dropoffStop.getSequenceOrder();

        if (requestedSeatId != null) {
            Seat seat = seatRepository.findByIdForUpdate(requestedSeatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat", "id", requestedSeatId));

            if (!seat.getTrip().getId().equals(trip.getId())) {
                throw new BookingException("Requested seat does not belong to trip: " + trip.getId());
            }

            if (!isSeatAvailableForSegment(seat, startSequence, endSequence)) {
                throw new BookingException("Requested seat " + seat.getSeatNumber() + " is already occupied for the requested segment");
            }

            return seat;
        }

        // Automatic seat selection: scan trip seats deterministically with pessimistic write lock
        List<Seat> seats = seatRepository.findByTripIdOrderByIdAscForUpdate(trip.getId());
        if (seats.isEmpty()) {
            throw new BookingException("No seats configured for trip id: " + trip.getId());
        }

        for (Seat seat : seats) {
            if (isSeatAvailableForSegment(seat, startSequence, endSequence)) {
                return seat;
            }
        }

        throw new BookingException("No seats available for the requested segment: " + pickupStop.getName() + " -> " + dropoffStop.getName());
    }

    /**
     * Linear interval-overlap check against all active (CONFIRMED) bookings for the given seat.
     * Overlap condition: requestedStart < existingEnd AND requestedEnd > existingStart.
     * Touching at a stop (e.g. 1->2 and 2->4) is NOT an overlap.
     */
    @Transactional(readOnly = true)
    public boolean isSeatAvailableForSegment(Seat seat, int startSequence, int endSequence) {
        List<Booking> activeBookings = bookingRepository.findBySeatIdAndStatus(seat.getId(), BookingStatus.CONFIRMED);

        for (Booking booking : activeBookings) {
            int existingStart = booking.getPickupStop().getSequenceOrder();
            int existingEnd = booking.getDropoffStop().getSequenceOrder();

            if (startSequence < existingEnd && endSequence > existingStart) {
                return false; // Overlap detected
            }
        }

        return true; // No overlap with any active booking
    }
}
