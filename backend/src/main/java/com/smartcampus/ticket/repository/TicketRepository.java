package com.smartcampus.ticket.repository;

import com.smartcampus.ticket.model.Ticket;
import com.smartcampus.ticket.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(Long userId);
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByAssignedTechnicianId(Long technicianId);
}
