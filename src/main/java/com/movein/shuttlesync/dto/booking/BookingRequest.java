package com.movein.shuttlesync.dto.booking;

import jakarta.validation.constraints.NotNull;

public class BookingRequest {

    @NotNull(message = "Trip ID is required")
    private Long tripId;

    @NotNull(message = "Pickup stop ID is required")
    private Long pickupStopId;

    @NotNull(message = "Dropoff stop ID is required")
    private Long dropoffStopId;

    private Long seatId;

    public BookingRequest() {
    }

    public BookingRequest(Long tripId, Long pickupStopId, Long dropoffStopId, Long seatId) {
        this.tripId = tripId;
        this.pickupStopId = pickupStopId;
        this.dropoffStopId = dropoffStopId;
        this.seatId = seatId;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
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

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }
}
