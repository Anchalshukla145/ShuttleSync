package com.movein.shuttlesync.service;

import com.movein.shuttlesync.dto.route.RouteResponse;
import com.movein.shuttlesync.entity.Route;
import com.movein.shuttlesync.exception.ResourceNotFoundException;
import com.movein.shuttlesync.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Transactional(readOnly = true)
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(this::mapToRouteResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RouteResponse getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        return mapToRouteResponse(route);
    }

    @Transactional(readOnly = true)
    public List<RouteResponse> searchRoutes(String origin, String destination) {
        return routeRepository.findByOriginIgnoreCaseAndDestinationIgnoreCase(origin, destination).stream()
                .map(this::mapToRouteResponse)
                .collect(Collectors.toList());
    }

    private RouteResponse mapToRouteResponse(Route route) {
        List<RouteResponse.StopDto> stopDtos = route.getStops().stream()
                .map(stop -> new RouteResponse.StopDto(
                        stop.getId(),
                        stop.getName(),
                        stop.getLocation(),
                        stop.getSequenceOrder()
                ))
                .collect(Collectors.toList());

        return new RouteResponse(
                route.getId(),
                route.getName(),
                route.getOrigin(),
                route.getDestination(),
                route.getDistanceKm(),
                route.getEstimatedDurationMinutes(),
                stopDtos
        );
    }
}
