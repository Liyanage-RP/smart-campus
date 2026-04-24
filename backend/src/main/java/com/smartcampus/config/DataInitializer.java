package com.smartcampus.config;

import com.smartcampus.ticket.model.*;
import com.smartcampus.ticket.repository.TicketRepository;
import com.smartcampus.booking.model.*;
import com.smartcampus.booking.repository.BookingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(TicketRepository ticketRepository, BookingRepository bookingRepository) {
        return args -> {
            if (ticketRepository.count() == 0) {
                Ticket t1 = new Ticket();
                t1.setUserId(1L);
                t1.setResourceLocation("Lecture Hall 1");
                t1.setCategory("Electrical");
                t1.setDescription("Lights are flickering near the podium.");
                t1.setPriority(TicketPriority.MEDIUM);
                t1.setStatus(TicketStatus.OPEN);
                ticketRepository.save(t1);

                Ticket t2 = new Ticket();
                t2.setUserId(2L);
                t2.setResourceLocation("Lab 3");
                t2.setCategory("IT Equipment");
                t2.setDescription("PC #4 has a blue screen of death.");
                t2.setPriority(TicketPriority.HIGH);
                t2.setStatus(TicketStatus.IN_PROGRESS);
                t2.setAssignedTechnicianId(501L);
                ticketRepository.save(t2);
            }

            if (bookingRepository.count() == 0) {
                Booking b1 = new Booking();
                b1.setUserId(1L);
                b1.setResourceId(101L); // Using a dummy ID for now
                b1.setStartTime(LocalDateTime.now().plusDays(1));
                b1.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
                b1.setPurpose("Project Meeting");
                b1.setStatus(BookingStatus.PENDING);
                bookingRepository.save(b1);
            }
        };
    }
}
