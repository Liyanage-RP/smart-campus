package com.smartcampus.booking.controller;

import com.smartcampus.booking.dto.BookingRequest;
import com.smartcampus.booking.model.Booking;
import com.smartcampus.booking.model.BookingStatus;
import com.smartcampus.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*") // Update to specific frontend URL in production
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            Booking booking = bookingService.createBooking(request);
            return new ResponseEntity<>(booking, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An unexpected error occurred."));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings(@RequestParam Long userId) {
        // In a real application, userId should be extracted from security context (e.g., JWT token)
        List<Booking> bookings = bookingService.getMyBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String statusStr = body.get("status");
            if (statusStr == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Status is required"));
            }
            BookingStatus status = BookingStatus.valueOf(statusStr.toUpperCase());
            Booking updatedBooking = bookingService.updateBookingStatus(id, status);
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid status or booking not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An unexpected error occurred."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An unexpected error occurred."));
        }
    }
}
