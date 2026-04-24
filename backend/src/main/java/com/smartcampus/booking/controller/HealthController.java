package com.smartcampus.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "application", "Smart Campus Backend",
            "version", "1.0.0",
            "message", "Server is running"
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }

    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> apiInfo() {
        return ResponseEntity.ok(Map.of(
            "name", "Smart Campus Booking API",
            "version", "1.0.0",
            "endpoints", Map.of(
                "bookings", "/api/bookings",
                "myBookings", "/api/bookings/my?userId={userId}",
                "bookingDetail", "/api/bookings/{id}"
            )
        ));
    }
}
