package com.movein.shuttlesync.controller;

import com.movein.shuttlesync.dto.route.RouteRequest;
import com.movein.shuttlesync.dto.route.RouteResponse;
import com.movein.shuttlesync.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(@Valid @RequestBody RouteRequest request) {
        RouteResponse createdRoute = routeService.createRoute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoute);
    }

    @GetMapping
    public ResponseEntity<List<RouteResponse>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponse> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RouteResponse>> searchRoutes(
            @RequestParam String origin,
            @RequestParam String destination
    ) {
        return ResponseEntity.ok(routeService.searchRoutes(origin, destination));
    }
}
