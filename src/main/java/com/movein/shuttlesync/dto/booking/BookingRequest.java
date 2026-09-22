package com.movein.shuttlesync.dto.booking;

import jakarta.validation.constraints.NotNull;

public class BookingRequest {

    @NotNull(message = "Trip ID is required")
    private Long tripId;

    private Long seatId;

    private Long pickupStopId;

    private Long dropoffStopId;

    public BookingRequest() {
    }

    public BookingRequest(Long tripId, Long seatId, Long pickupStopId, Long dropoffStopId) {
        this.tripId = tripId;
        this.seatId = seatId;
        this.pickupStopId = pickupStopId;
        this.dropoffStopId = dropoffStopId;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Long getPickupStopId() {
        return pickupStopId;
    }

    public void setPickupStopId(Long pickupStopId) {
        this.pickupStopId = pickupStopId;
    }

    public Long getDropoffStopId() {
        return dropoffStopId;
    }

    public void setDropoffStopId(Long dropoffStopId) {
        this.dropoffStopId = dropoffStopId;
    }
}
