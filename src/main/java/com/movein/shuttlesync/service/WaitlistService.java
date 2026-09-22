package com.movein.shuttlesync.service;

import com.movein.shuttlesync.common.enums.WaitlistStatus;
import com.movein.shuttlesync.entity.Stop;
import com.movein.shuttlesync.entity.Trip;
import com.movein.shuttlesync.entity.User;
import com.movein.shuttlesync.entity.WaitlistEntry;
import com.movein.shuttlesync.exception.ResourceNotFoundException;
import com.movein.shuttlesync.repository.StopRepository;
import com.movein.shuttlesync.repository.TripRepository;
import com.movein.shuttlesync.repository.UserRepository;
import com.movein.shuttlesync.repository.WaitlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final StopRepository stopRepository;

    public WaitlistService(WaitlistRepository waitlistRepository, TripRepository tripRepository, UserRepository userRepository, StopRepository stopRepository) {
        this.waitlistRepository = waitlistRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.stopRepository = stopRepository;
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
}
