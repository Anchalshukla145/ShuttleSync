package com.movein.shuttlesync.service;

import com.movein.shuttlesync.common.enums.BookingStatus;
import com.movein.shuttlesync.common.enums.WaitlistStatus;
import com.movein.shuttlesync.entity.*;
import com.movein.shuttlesync.exception.ResourceNotFoundException;
import com.movein.shuttlesync.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final StopRepository stopRepository;
    private final BookingRepository bookingRepository;
    private final SeatAllocationService seatAllocationService;

    public WaitlistService(WaitlistRepository waitlistRepository,
                           TripRepository tripRepository,
                           UserRepository userRepository,
                           StopRepository stopRepository,
                           BookingRepository bookingRepository,
                           SeatAllocationService seatAllocationService) {
        this.waitlistRepository = waitlistRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.stopRepository = stopRepository;
        this.bookingRepository = bookingRepository;
        this.seatAllocationService = seatAllocationService;
    }

    @Transactional
    public WaitlistEntry joinWaitlist(Long userId, Long tripId, Long pickupStopId, Long dropoffStopId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));

        Stop pickupStop = pickupStopId != null ? stopRepository.findById(pickupStopId).orElse(null) : null;
        Stop dropoffStop = dropoffStopId != null ? stopRepository.findById(dropoffStopId).orElse(null) : null;

        int currentCount = waitlistRepository.countByTripIdAndStatus(tripId, WaitlistStatus.PENDING);

        WaitlistEntry entry = new WaitlistEntry();
        entry.setUser(user);
        entry.setTrip(trip);
        entry.setPickupStop(pickupStop);
        entry.setDropoffStop(dropoffStop);
        entry.setPriorityOrder(currentCount + 1);
        entry.setStatus(WaitlistStatus.PENDING);

        return waitlistRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public List<WaitlistEntry> getWaitlistForTrip(Long tripId) {
        return waitlistRepository.findByTripIdAndStatusOrderByPriorityOrderAsc(tripId, WaitlistStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<WaitlistEntry> getUserWaitlist(Long userId) {
        return waitlistRepository.findByUserId(userId);
    }

    @Transactional
    public void promoteWaitlistedPassenger(Trip trip, Seat seat) {
        if (trip == null || seat == null) {
            return;
        }

        List<WaitlistEntry> pendingEntries = waitlistRepository
                .findByTripIdAndStatusOrderByPriorityOrderAsc(trip.getId(), WaitlistStatus.PENDING);

        for (WaitlistEntry candidate : pendingEntries) {
            Stop pickupStop = candidate.getPickupStop();
            Stop dropoffStop = candidate.getDropoffStop();

            if (pickupStop == null || dropoffStop == null) {
                continue;
            }

            int startSequence = pickupStop.getSequenceOrder();
            int endSequence = dropoffStop.getSequenceOrder();

            if (seatAllocationService.isSeatAvailableForSegment(seat, startSequence, endSequence)) {
                Booking booking = new Booking();
                booking.setBookingReference("BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                booking.setUser(candidate.getUser());
                booking.setTrip(trip);
                booking.setSeat(seat);
                booking.setPickupStop(pickupStop);
                booking.setDropoffStop(dropoffStop);
                booking.setStatus(BookingStatus.CONFIRMED);
                booking.setBookingTime(LocalDateTime.now());
                bookingRepository.save(booking);

                candidate.setStatus(WaitlistStatus.ALLOCATED);
                waitlistRepository.save(candidate);

                break;
            }
        }
    }
}
