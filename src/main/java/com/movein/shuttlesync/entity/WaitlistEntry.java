package com.movein.shuttlesync.entity;

import com.movein.shuttlesync.common.enums.WaitlistStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "waitlist_entries")
public class WaitlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_stop_id")
    private Stop pickupStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dropoff_stop_id")
    private Stop dropoffStop;

    @Column(nullable = false)
    private Integer priorityOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitlistStatus status = WaitlistStatus.PENDING;

    private LocalDateTime requestedAt = LocalDateTime.now();

    public WaitlistEntry() {
    }

    public WaitlistEntry(Long id, User user, Trip trip, Stop pickupStop, Stop dropoffStop, Integer priorityOrder, WaitlistStatus status) {
        this.id = id;
        this.user = user;
        this.trip = trip;
        this.pickupStop = pickupStop;
        this.dropoffStop = dropoffStop;
        this.priorityOrder = priorityOrder;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getPriorityOrder() {
        return priorityOrder;
    }

    public void setPriorityOrder(Integer priorityOrder) {
        this.priorityOrder = priorityOrder;
    }

    public WaitlistStatus getStatus() {
        return status;
    }

    public void setStatus(WaitlistStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
}
