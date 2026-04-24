package com.smartcampus.ticket.service;

import com.smartcampus.ticket.dto.CommentRequest;
import com.smartcampus.ticket.dto.TicketRequest;
import com.smartcampus.ticket.model.*;
import com.smartcampus.ticket.repository.TicketAttachmentRepository;
import com.smartcampus.ticket.repository.TicketCommentRepository;
import com.smartcampus.ticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private static final int MAX_ATTACHMENTS = 3;
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketAttachmentRepository attachmentRepository;

    @Autowired
    private TicketCommentRepository commentRepository;

    @Value("${app.upload.dir:uploads/tickets}")
    private String uploadDir;

    // ─── Ticket CRUD ───────────────────────────────────────────────

    public Ticket createTicket(TicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setUserId(request.getUserId());
        ticket.setResourceLocation(request.getResourceLocation());
        ticket.setCategory(request.getCategory());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setContactDetails(request.getContactDetails());
        ticket.setStatus(TicketStatus.OPEN);
        return ticketRepository.save(ticket);
    }

    public List<Ticket> getMyTickets(Long userId) {
        return ticketRepository.findByUserId(userId);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public Ticket updateTicketStatus(Long id, TicketStatus newStatus, String resolutionNotes, String adminReason) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with id: " + id));

        ticket.setStatus(newStatus);
        if (resolutionNotes != null && !resolutionNotes.trim().isEmpty()) {
            ticket.setResolutionNotes(resolutionNotes);
        }
        if (adminReason != null && !adminReason.trim().isEmpty()) {
            ticket.setAdminReason(adminReason);
        }
        return ticketRepository.save(ticket);
    }

    public Ticket assignTechnician(Long ticketId, Long technicianId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with id: " + ticketId));
        ticket.setAssignedTechnicianId(technicianId);
        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }
        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new IllegalArgumentException("Ticket not found with id: " + id);
        }
        ticketRepository.deleteById(id);
    }

    // ─── Attachments ───────────────────────────────────────────────

    public TicketAttachment addAttachment(Long ticketId, MultipartFile file) throws IOException {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with id: " + ticketId));

        long currentCount = attachmentRepository.countByTicketId(ticketId);
        if (currentCount >= MAX_ATTACHMENTS) {
            throw new IllegalStateException("Maximum of " + MAX_ATTACHMENTS + " attachments allowed per ticket.");
        }

        if (!ALLOWED_FILE_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Only image files (JPEG, PNG, GIF, WEBP) are allowed.");
        }

        // Save file to upload directory
        File uploadDirectory = new File(uploadDir + "/" + ticketId);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDirectory, fileName);
        file.transferTo(dest);

        TicketAttachment attachment = new TicketAttachment();
        attachment.setTicket(ticket);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setFilePath(dest.getPath());
        return attachmentRepository.save(attachment);
    }

    // ─── Comments ─────────────────────────────────────────────────

    public TicketComment addComment(Long ticketId, CommentRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with id: " + ticketId));

        TicketComment comment = new TicketComment();
        comment.setTicket(ticket);
        comment.setAuthorId(request.getAuthorId());
        comment.setAuthorName(request.getAuthorName());
        comment.setContent(request.getContent());
        return commentRepository.save(comment);
    }

    public List<TicketComment> getComments(Long ticketId) {
        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }

    public TicketComment editComment(Long commentId, Long requestingUserId, String newContent) {
        TicketComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        if (!comment.getAuthorId().equals(requestingUserId)) {
            throw new SecurityException("You are not authorised to edit this comment.");
        }
        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, Long requestingUserId) {
        TicketComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        if (!comment.getAuthorId().equals(requestingUserId)) {
            throw new SecurityException("You are not authorised to delete this comment.");
        }
        commentRepository.deleteById(commentId);
    }
}
