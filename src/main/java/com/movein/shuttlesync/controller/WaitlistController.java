package com.movein.shuttlesync.controller;

import com.movein.shuttlesync.entity.WaitlistEntry;
import com.movein.shuttlesync.service.WaitlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waitlist")
public class WaitlistController {

    private final WaitlistService waitlistService;

    public WaitlistController(WaitlistService waitlistService) {
        this.waitlistService = waitlistService;
    }

    @PostMapping("/join")
    public ResponseEntity<WaitlistEntry> joinWaitlist(
            @RequestParam Long userId,
            @RequestParam Long tripId,
            @RequestParam(required = false) Long pickupStopId,
            @RequestParam(required = false) Long dropoffStopId
    ) {
        return ResponseEntity.ok(waitlistService.joinWaitlist(userId, tripId, pickupStopId, dropoffStopId));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<WaitlistEntry>> getWaitlistForTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(waitlistService.getWaitlistForTrip(tripId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WaitlistEntry>> getUserWaitlist(@PathVariable Long userId) {
        return ResponseEntity.ok(waitlistService.getUserWaitlist(userId));
    }
}
