package com.movein.shuttlesync.dto.booking;

import com.movein.shuttlesync.common.enums.BookingStatus;
import java.time.LocalDateTime;

public class BookingResponse {

    private Long id;
    private String bookingReference;
    private Long userId;
    private String username;
    private Long tripId;
    private String seatNumber;
    private String pickupStopName;
    private String dropoffStopName;
    private BookingStatus status;
    private LocalDateTime bookingTime;

    public BookingResponse() {
    }

    public BookingResponse(Long id, String bookingReference, Long userId, String username, Long tripId, String seatNumber, String pickupStopName, String dropoffStopName, BookingStatus status, LocalDateTime bookingTime) {
        this.id = id;
        this.bookingReference = bookingReference;
        this.userId = userId;
        this.username = username;
        this.tripId = tripId;
        this.seatNumber = seatNumber;
        this.pickupStopName = pickupStopName;
        this.dropoffStopName = dropoffStopName;
        this.status = status;
        this.bookingTime = bookingTime;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getPickupStopName() {
        return pickupStopName;
    }

    public void setPickupStopName(String pickupStopName) {
        this.pickupStopName = pickupStopName;
    }

    public String getDropoffStopName() {
        return dropoffStopName;
    }

    public void setDropoffStopName(String dropoffStopName) {
        this.dropoffStopName = dropoffStopName;
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
