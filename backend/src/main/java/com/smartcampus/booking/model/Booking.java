package com.smartcampus.booking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long resourceId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    public Booking() {
    }

    public Booking(Long resourceId, Long userId, LocalDateTime startTime, LocalDateTime endTime, BookingStatus status) {
        this.resourceId = resourceId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
