package com.movein.shuttlesync.dto.route;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

public class RouteRequest {

    @NotBlank(message = "Route name is required")
    private String name;

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    private Double distanceKm;

    private Integer estimatedDurationMinutes;

    @NotEmpty(message = "Stops list cannot be empty")
    @Valid
    private List<StopRequest> stops;

    public static class StopRequest {

        @NotBlank(message = "Stop name is required")
        private String name;

        private String location;

        @NotNull(message = "Sequence order is required")
        private Integer sequenceOrder;

        @NotNull(message = "Arrival time is required")
        private LocalTime arrivalTime;

        public StopRequest() {
        }

        public StopRequest(String name, String location, Integer sequenceOrder, LocalTime arrivalTime) {
            this.name = name;
            this.location = location;
            this.sequenceOrder = sequenceOrder;
            this.arrivalTime = arrivalTime;
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
    }

    public RouteRequest() {
    }

    public RouteRequest(String name, String origin, String destination, Double distanceKm, Integer estimatedDurationMinutes, List<StopRequest> stops) {
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.stops = stops;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public List<StopRequest> getStops() {
        return stops;
    }

    public void setStops(List<StopRequest> stops) {
        this.stops = stops;
    }
}
