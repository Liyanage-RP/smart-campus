package com.smartcampus.booking.service;

import com.smartcampus.booking.dto.BookingRequest;
import com.smartcampus.booking.model.Booking;
import com.smartcampus.booking.model.BookingStatus;
import com.smartcampus.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequest validRequest;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.now().plusHours(1);
        endTime = LocalDateTime.now().plusHours(3);

        validRequest = new BookingRequest();
        validRequest.setUserId(1L);
        validRequest.setResourceId(101L);
        validRequest.setStartTime(startTime);
        validRequest.setEndTime(endTime);
        validRequest.setPurpose("Team Meeting");
        validRequest.setExpectedAttendees(10);
    }

    // ─── createBooking Tests ───────────────────────────────────────────

    @Test
    void createBooking_withNoConflicts_shouldSaveWithPendingStatus() {
        when(bookingRepository.findOverlappingBookings(anyLong(), any(), any(), anyList()))
                .thenReturn(Collections.emptyList());

        Booking savedBooking = new Booking(101L, 1L, startTime, endTime, BookingStatus.PENDING, "Team Meeting", 10);
        savedBooking.setId(1L);
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        Booking result = bookingService.createBooking(validRequest);

        assertNotNull(result);
        assertEquals(BookingStatus.PENDING, result.getStatus());
        assertEquals("Team Meeting", result.getPurpose());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_withConflict_shouldThrowIllegalStateException() {
        Booking conflicting = new Booking(101L, 2L, startTime, endTime, BookingStatus.APPROVED, "Other Meeting", 5);
        when(bookingRepository.findOverlappingBookings(anyLong(), any(), any(), anyList()))
                .thenReturn(Arrays.asList(conflicting));

        assertThrows(IllegalStateException.class, () -> bookingService.createBooking(validRequest));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_withEndBeforeStart_shouldThrowIllegalArgumentException() {
        validRequest.setStartTime(endTime);
        validRequest.setEndTime(startTime); // Reversed

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(validRequest));
        verify(bookingRepository, never()).findOverlappingBookings(any(), any(), any(), any());
    }

    @Test
    void createBooking_withEqualStartAndEnd_shouldThrowIllegalArgumentException() {
        validRequest.setEndTime(startTime); // Same as start

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(validRequest));
    }

    // ─── getMyBookings Tests ─────────────────────────────────────────

    @Test
    void getMyBookings_shouldReturnUserBookings() {
        Booking b1 = new Booking(101L, 1L, startTime, endTime, BookingStatus.PENDING, "Meeting 1", 5);
        Booking b2 = new Booking(102L, 1L, startTime, endTime, BookingStatus.APPROVED, "Meeting 2", 8);
        when(bookingRepository.findByUserId(1L)).thenReturn(Arrays.asList(b1, b2));

        List<Booking> result = bookingService.getMyBookings(1L);

        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getMyBookings_withNoBookings_shouldReturnEmptyList() {
        when(bookingRepository.findByUserId(99L)).thenReturn(Collections.emptyList());
        List<Booking> result = bookingService.getMyBookings(99L);
        assertTrue(result.isEmpty());
    }

    // ─── getBookingById Tests ────────────────────────────────────────

    @Test
    void getBookingById_withValidId_shouldReturnBooking() {
        Booking booking = new Booking(101L, 1L, startTime, endTime, BookingStatus.PENDING, "Meeting", 5);
        booking.setId(1L);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Optional<Booking> result = bookingService.getBookingById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void getBookingById_withInvalidId_shouldReturnEmpty() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Booking> result = bookingService.getBookingById(999L);
        assertFalse(result.isPresent());
    }

    // ─── updateBookingStatus Tests ───────────────────────────────────

    @Test
    void updateBookingStatus_shouldUpdateStatusAndReason() {
        Booking booking = new Booking(101L, 1L, startTime, endTime, BookingStatus.PENDING, "Meeting", 5);
        booking.setId(1L);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        Booking result = bookingService.updateBookingStatus(1L, BookingStatus.APPROVED, null, null);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void updateBookingStatus_withRejection_shouldSaveReason() {
        Booking booking = new Booking(101L, 1L, startTime, endTime, BookingStatus.PENDING, "Meeting", 5);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Booking result = bookingService.updateBookingStatus(1L, BookingStatus.REJECTED, null, "Resource unavailable");

        assertEquals(BookingStatus.REJECTED, result.getStatus());
        assertEquals("Resource unavailable", result.getAdminReason());
    }

    @Test
    void updateBookingStatus_withInvalidId_shouldThrow() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.updateBookingStatus(999L, BookingStatus.APPROVED, null, null));
    }

    // ─── deleteBooking Tests ─────────────────────────────────────────

    @Test
    void deleteBooking_withValidId_shouldDelete() {
        when(bookingRepository.existsById(1L)).thenReturn(true);
        bookingService.deleteBooking(1L);
        verify(bookingRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBooking_withInvalidId_shouldThrow() {
        when(bookingRepository.existsById(999L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> bookingService.deleteBooking(999L));
        verify(bookingRepository, never()).deleteById(any());
    }

    // ─── getAllBookings Tests ─────────────────────────────────────────

    @Test
    void getAllBookings_shouldReturnAllBookings() {
        Booking b1 = new Booking(101L, 1L, startTime, endTime, BookingStatus.PENDING, "A", 5);
        Booking b2 = new Booking(102L, 2L, startTime, endTime, BookingStatus.APPROVED, "B", 3);
        when(bookingRepository.findAll()).thenReturn(Arrays.asList(b1, b2));

        List<Booking> result = bookingService.getAllBookings();

        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findAll();
    }
}
