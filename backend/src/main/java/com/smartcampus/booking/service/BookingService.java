package com.smartcampus.booking.service;

import com.smartcampus.booking.dto.BookingRequest;
import com.smartcampus.booking.model.Booking;
import com.smartcampus.booking.model.BookingStatus;
import com.smartcampus.booking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public Booking createBooking(BookingRequest request) {
        // Validate time
        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().isEqual(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Check for conflicts
        List<BookingStatus> activeStatuses = Arrays.asList(BookingStatus.PENDING, BookingStatus.APPROVED);
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                request.getResourceId(),
                request.getStartTime(),
                request.getEndTime(),
                activeStatuses
        );

        if (!overlappingBookings.isEmpty()) {
            throw new IllegalStateException("The resource is already booked for the selected time range.");
        }

        Booking booking = new Booking(
                request.getResourceId(),
                request.getUserId(),
                request.getStartTime(),
                request.getEndTime(),
                BookingStatus.PENDING,
                request.getPurpose(),
                request.getExpectedAttendees()
        );

        return bookingRepository.save(booking);
    }

    public List<Booking> getMyBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking updateBookingStatus(Long id, BookingStatus newStatus, String adminReason) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        booking.setStatus(newStatus);
        if (adminReason != null && !adminReason.trim().isEmpty()) {
            booking.setAdminReason(adminReason);
        }
        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new IllegalArgumentException("Booking not found");
        }
        bookingRepository.deleteById(id);
    }
}
