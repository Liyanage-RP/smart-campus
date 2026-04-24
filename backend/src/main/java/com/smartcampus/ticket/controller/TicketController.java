package com.smartcampus.ticket.controller;

import com.smartcampus.ticket.dto.CommentRequest;
import com.smartcampus.ticket.dto.TicketRequest;
import com.smartcampus.ticket.model.*;
import com.smartcampus.ticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // POST /api/tickets – Create a new incident ticket
    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody TicketRequest request) {
        try {
            Ticket ticket = ticketService.createTicket(request);
            return new ResponseEntity<>(ticket, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An unexpected error occurred."));
        }
    }

    // GET /api/tickets/my – Get all tickets for a specific user
    @GetMapping("/my")
    public ResponseEntity<List<Ticket>> getMyTickets(@RequestParam Long userId) {
        return ResponseEntity.ok(ticketService.getMyTickets(userId));
    }

    // GET /api/tickets/all – Admin: get all tickets
    @GetMapping("/all")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    // GET /api/tickets/{id} – Get single ticket by ID
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/tickets/{id}/status – Update ticket status (Admin/Technician)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateTicketStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            TicketStatus status = TicketStatus.valueOf(body.get("status").toUpperCase());
            String resolutionNotes = body.get("resolutionNotes");
            String adminReason = body.get("adminReason");
            Ticket updated = ticketService.updateTicketStatus(id, status, resolutionNotes, adminReason);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid status or ticket not found: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An unexpected error occurred."));
        }
    }

    // PUT /api/tickets/{id}/assign – Assign a technician to a ticket
    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignTechnician(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        try {
            Long technicianId = body.get("technicianId");
            Ticket updated = ticketService.assignTechnician(id, technicianId);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An unexpected error occurred."));
        }
    }

    // DELETE /api/tickets/{id} – Delete a ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable Long id) {
        try {
            ticketService.deleteTicket(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An unexpected error occurred."));
        }
    }

    // POST /api/tickets/{id}/attachments – Upload an image attachment (max 3)
    @PostMapping("/{id}/attachments")
    public ResponseEntity<?> addAttachment(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            TicketAttachment attachment = ticketService.addAttachment(id, file);
            return new ResponseEntity<>(attachment, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to upload attachment."));
        }
    }

    // POST /api/tickets/{id}/comments – Add a comment to a ticket
    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long id, @RequestBody CommentRequest request) {
        try {
            TicketComment comment = ticketService.addComment(id, request);
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An unexpected error occurred."));
        }
    }

    // GET /api/tickets/{id}/comments – Get all comments for a ticket
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<TicketComment>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getComments(id));
    }

    // PUT /api/tickets/{ticketId}/comments/{commentId} – Edit a comment (owner only)
    @PutMapping("/{ticketId}/comments/{commentId}")
    public ResponseEntity<?> editComment(
            @PathVariable Long ticketId,
            @PathVariable Long commentId,
            @RequestBody Map<String, Object> body) {
        try {
            Long requestingUserId = Long.valueOf(body.get("userId").toString());
            String newContent = body.get("content").toString();
            TicketComment updated = ticketService.editComment(commentId, requestingUserId, newContent);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An unexpected error occurred."));
        }
    }

    // DELETE /api/tickets/{ticketId}/comments/{commentId} – Delete a comment (owner only)
    @DeleteMapping("/{ticketId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long ticketId,
            @PathVariable Long commentId,
            @RequestParam Long userId) {
        try {
            ticketService.deleteComment(commentId, userId);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An unexpected error occurred."));
        }
    }
}
