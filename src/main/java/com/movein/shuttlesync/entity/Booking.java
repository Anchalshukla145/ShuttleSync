package com.movein.shuttlesync.entity;

import com.movein.shuttlesync.common.enums.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_stop_id")
    private Stop pickupStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dropoff_stop_id")
    private Stop dropoffStop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.CONFIRMED;

    private LocalDateTime bookingTime = LocalDateTime.now();

    public Booking() {
    }

    public Booking(Long id, String bookingReference, User user, Trip trip, Seat seat, Stop pickupStop, Stop dropoffStop, BookingStatus status) {
        this.id = id;
        this.bookingReference = bookingReference;
        this.user = user;
        this.trip = trip;
        this.seat = seat;
        this.pickupStop = pickupStop;
        this.dropoffStop = dropoffStop;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public Stop getPickupStop() {
        return pickupStop;
    }

    public void setPickupStop(Stop pickupStop) {
        this.pickupStop = pickupStop;
    }

    public Stop getDropoffStop() {
        return dropoffStop;
    }

    public void setDropoffStop(Stop dropoffStop) {
        this.dropoffStop = dropoffStop;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }
}
