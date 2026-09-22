package com.movein.shuttlesync.service;

import com.movein.shuttlesync.common.enums.BookingStatus;
import com.movein.shuttlesync.dto.booking.BookingRequest;
import com.movein.shuttlesync.dto.booking.BookingResponse;
import com.movein.shuttlesync.entity.*;
import com.movein.shuttlesync.exception.BookingException;
import com.movein.shuttlesync.exception.ResourceNotFoundException;
import com.movein.shuttlesync.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", request.getTripId()));

        if (trip.getAvailableSeats() != null && trip.getAvailableSeats() <= 0) {
            throw new BookingException("No available seats on this trip");
        }

        Seat seat = seatAllocationService.allocateSeat(trip, request.getSeatId());

        Stop pickupStop = request.getPickupStopId() != null
                ? stopRepository.findById(request.getPickupStopId()).orElse(null)
                : null;

        Stop dropoffStop = request.getDropoffStopId() != null
                ? stopRepository.findById(request.getDropoffStopId()).orElse(null)
                : null;

        Booking booking = new Booking();
        booking.setBookingReference("BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setUser(user);
        booking.setTrip(trip);
        booking.setSeat(seat);
        booking.setPickupStop(pickupStop);
        booking.setDropoffStop(dropoffStop);
        booking.setStatus(BookingStatus.CONFIRMED);

        if (trip.getAvailableSeats() != null) {
            trip.setAvailableSeats(trip.getAvailableSeats() - 1);
            tripRepository.save(trip);
        }

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

        booking.setStatus(BookingStatus.CANCELLED);
        seatAllocationService.releaseSeat(booking.getSeat());

        Trip trip = booking.getTrip();
        if (trip.getAvailableSeats() != null) {
            trip.setAvailableSeats(trip.getAvailableSeats() + 1);
            tripRepository.save(trip);
        }

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
