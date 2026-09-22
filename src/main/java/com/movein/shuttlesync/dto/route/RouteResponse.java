package com.movein.shuttlesync.dto.route;

import java.util.List;

public class RouteResponse {

    private Long id;
    private String name;
    private String origin;
    private String destination;
    private Double distanceKm;
    private Integer estimatedDurationMinutes;
    private List<StopDto> stops;

    public static class StopDto {
        private Long id;
        private String name;
        private String location;
        private Integer sequenceOrder;

        public StopDto() {
        }

        public StopDto(Long id, String name, String location, Integer sequenceOrder) {
            this.id = id;
            this.name = name;
            this.location = location;
            this.sequenceOrder = sequenceOrder;
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
    }

    public RouteResponse() {
    }

    public RouteResponse(Long id, String name, String origin, String destination, Double distanceKm, Integer estimatedDurationMinutes, List<StopDto> stops) {
        this.id = id;
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.stops = stops;
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

    public List<StopDto> getStops() {
        return stops;
    }

    public void setStops(List<StopDto> stops) {
        this.stops = stops;
    }
}
