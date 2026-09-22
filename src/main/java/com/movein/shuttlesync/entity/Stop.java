package com.movein.shuttlesync.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "stops")
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String location;

    @Column(nullable = false)
    private Integer sequenceOrder;

    private LocalTime arrivalTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    public Stop() {
    }

    public Stop(Long id, String name, String location, Integer sequenceOrder, Route route) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.sequenceOrder = sequenceOrder;
        this.route = route;
    }

    public Stop(Long id, String name, String location, Integer sequenceOrder, LocalTime arrivalTime, Route route) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.sequenceOrder = sequenceOrder;
        this.arrivalTime = arrivalTime;
        this.route = route;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
