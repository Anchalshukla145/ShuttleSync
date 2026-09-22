package com.movein.shuttlesync.service;

import com.movein.shuttlesync.common.enums.BookingStatus;
import com.movein.shuttlesync.common.enums.TripStatus;
import com.movein.shuttlesync.dto.booking.BookingRequest;
import com.movein.shuttlesync.dto.booking.BookingResponse;
import com.movein.shuttlesync.entity.*;
import com.movein.shuttlesync.exception.BookingException;
import com.movein.shuttlesync.exception.ResourceNotFoundException;
import com.movein.shuttlesync.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final StopRepository stopRepository;
    private final SeatAllocationService seatAllocationService;

    public BookingService(BookingRepository bookingRepository,
                          TripRepository tripRepository,
                          UserRepository userRepository,
                          StopRepository stopRepository,
                          SeatAllocationService seatAllocationService) {
        this.bookingRepository = bookingRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.stopRepository = stopRepository;
        this.seatAllocationService = seatAllocationService;
    }

    @Transactional
    public BookingResponse createBooking(Long userId, BookingRequest request) {
        // 1. Validate User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // 2. Validate Trip
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", request.getTripId()));

        if (trip.getStatus() != TripStatus.SCHEDULED) {
            throw new BookingException("Trip is not in a bookable status: " + trip.getStatus());
        }

        // 3. Validate Pickup and Dropoff Stops
        Stop pickupStop = stopRepository.findById(request.getPickupStopId())
                .orElseThrow(() -> new ResourceNotFoundException("Stop", "id", request.getPickupStopId()));

        Stop dropoffStop = stopRepository.findById(request.getDropoffStopId())
                .orElseThrow(() -> new ResourceNotFoundException("Stop", "id", request.getDropoffStopId()));

        // 4. Validate stops belong to the trip route
        Long routeId = trip.getRoute().getId();
        if (!pickupStop.getRoute().getId().equals(routeId)) {
            throw new BookingException("Pickup stop does not belong to the trip route");
        }
        if (!dropoffStop.getRoute().getId().equals(routeId)) {
            throw new BookingException("Dropoff stop does not belong to the trip route");
        }

        // 5. Validate stop sequence order
        if (pickupStop.getSequenceOrder() >= dropoffStop.getSequenceOrder()) {
            throw new BookingException("Pickup stop must come before dropoff stop in the route sequence");
        }

        // 6. Allocate seat using segment-based overlap check & pessimistic lock
        Seat seat = seatAllocationService.allocateSeat(trip, pickupStop, dropoffStop, request.getSeatId());

        // 7. Create and persist booking
        Booking booking = new Booking();
        booking.setBookingReference("BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setUser(user);
        booking.setTrip(trip);
        booking.setSeat(seat);
        booking.setPickupStop(pickupStop);
        booking.setDropoffStop(dropoffStop);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookingTime(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);
        return mapToBookingResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingByReference(String reference) {
        Booking booking = bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "reference", reference));
        return mapToBookingResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getBookingReference(),
                booking.getUser().getId(),
                booking.getUser().getUsername(),
                booking.getTrip().getId(),
                booking.getSeat() != null ? booking.getSeat().getSeatNumber() : null,
                booking.getPickupStop() != null ? booking.getPickupStop().getName() : null,
                booking.getDropoffStop() != null ? booking.getDropoffStop().getName() : null,
                booking.getStatus(),
                booking.getBookingTime()
        );
    }
}
