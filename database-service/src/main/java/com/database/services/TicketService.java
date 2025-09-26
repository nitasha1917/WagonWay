package com.database.services;

import com.database.models.Ticket;
import com.database.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Ticket createTicket(Ticket ticket) {
        // Set defaults
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        if (ticket.getBookingStatus() == null || ticket.getBookingStatus().isEmpty()) {
            ticket.setBookingStatus("PENDING");
        }
        if (ticket.getPaymentStatus() == null || ticket.getPaymentStatus().isEmpty()) {
            ticket.setPaymentStatus("PENDING");
        }

        return ticketRepository.save(ticket);
    }

    public Optional<Ticket> findById(String id) {
        return ticketRepository.findById(id);
    }

    public Optional<Ticket> findByTicketNumber(String ticketNumber) {
        return ticketRepository.findByTicketNumber(ticketNumber);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Ticket> getTicketsByUserId(String userId) {
        return ticketRepository.findByUserId(userId);
    }

    public List<Ticket> getTicketsByEventId(String eventId) {
        return ticketRepository.findByEventId(eventId);
    }

    public List<Ticket> getUserActiveBookings(String userId) {
        return ticketRepository.findActiveBookingsByUserId(userId);
    }

    public List<Ticket> getEventConfirmedBookings(String eventId) {
        return ticketRepository.findConfirmedBookingsByEventId(eventId);
    }

    public List<Ticket> getTicketsByStatus(String status) {
        return ticketRepository.findByBookingStatus(status);
    }

    public List<Ticket> getTicketsByPaymentStatus(String paymentStatus) {
        return ticketRepository.findByPaymentStatus(paymentStatus);
    }

    public Ticket updateTicket(String id, Ticket updatedTicket) {
        Optional<Ticket> existingTicket = ticketRepository.findById(id);
        if (existingTicket.isEmpty()) {
            throw new RuntimeException("Ticket not found");
        }

        Ticket ticket = existingTicket.get();

        // Update fields if provided
        if (updatedTicket.getQuantity() != null) {
            ticket.setQuantity(updatedTicket.getQuantity());
        }
        if (updatedTicket.getTotalAmount() != null) {
            ticket.setTotalAmount(updatedTicket.getTotalAmount());
        }
        if (updatedTicket.getBookingStatus() != null) {
            ticket.setBookingStatus(updatedTicket.getBookingStatus());
            if ("CANCELLED".equals(updatedTicket.getBookingStatus())) {
                ticket.setCancellationTime(LocalDateTime.now());
            }
        }
        if (updatedTicket.getPaymentStatus() != null) {
            ticket.setPaymentStatus(updatedTicket.getPaymentStatus());
            if ("COMPLETED".equals(updatedTicket.getPaymentStatus())) {
                ticket.setPaymentTime(LocalDateTime.now());
            }
        }
        if (updatedTicket.getSeatNumbers() != null) {
            ticket.setSeatNumbers(updatedTicket.getSeatNumbers());
        }
        if (updatedTicket.getSeatType() != null) {
            ticket.setSeatType(updatedTicket.getSeatType());
        }
        if (updatedTicket.getQrCode() != null) {
            ticket.setQrCode(updatedTicket.getQrCode());
        }
        if (updatedTicket.getCancellationReason() != null) {
            ticket.setCancellationReason(updatedTicket.getCancellationReason());
        }
        if (updatedTicket.getPaymentMethod() != null) {
            ticket.setPaymentMethod(updatedTicket.getPaymentMethod());
        }
        if (updatedTicket.getTransactionId() != null) {
            ticket.setTransactionId(updatedTicket.getTransactionId());
        }

        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    public Ticket confirmTicket(String id) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if (ticketOpt.isEmpty()) {
            throw new RuntimeException("Ticket not found");
        }

        Ticket ticket = ticketOpt.get();
        ticket.setBookingStatus("CONFIRMED");
        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    public Ticket cancelTicket(String id, String reason) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(id);
        if (ticketOpt.isEmpty()) {
            throw new RuntimeException("Ticket not found");
        }

        Ticket ticket = ticketOpt.get();
        ticket.setBookingStatus("CANCELLED");
        ticket.setCancellationTime(LocalDateTime.now());
        ticket.setCancellationReason(reason);
        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    public void deleteTicket(String id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Ticket not found");
        }
        ticketRepository.deleteById(id);
    }

    public List<Ticket> getTicketsByDateRange(LocalDateTime start, LocalDateTime end) {
        return ticketRepository.findByBookingTimeBetween(start, end);
    }

    public List<Ticket> getUserBookingsByDateRange(String userId, LocalDateTime start, LocalDateTime end) {
        return ticketRepository.findUserBookingsBetweenDates(userId, start, end);
    }

    public List<Ticket> getCompletedBookings() {
        return ticketRepository.findCompletedBookings();
    }

    public List<Ticket> getCompletedBookingsByEvent(String eventId) {
        return ticketRepository.findCompletedBookingsByEventId(eventId);
    }

    // Statistics
    public Long getTotalTickets() {
        return ticketRepository.count();
    }

    public Long getConfirmedTicketsCount() {
        return ticketRepository.countByBookingStatus("CONFIRMED");
    }

    public Long getCancelledTicketsCount() {
        return ticketRepository.countByBookingStatus("CANCELLED");
    }

    public Long getEventConfirmedTicketsCount(String eventId) {
        return ticketRepository.countConfirmedTicketsByEventId(eventId);
    }

    public Long getUserConfirmedTicketsCount(String userId) {
        return ticketRepository.countByUserIdAndBookingStatus(userId, "CONFIRMED");
    }

    public Long getConfirmedBookingsBetween(LocalDateTime start, LocalDateTime end) {
        return ticketRepository.countConfirmedBookingsBetween(start, end);
    }

    public BigDecimal getTotalRevenue() {
        List<Ticket> completedBookings = getCompletedBookings();
        return completedBookings.stream()
                .map(Ticket::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getEventRevenue(String eventId) {
        List<Ticket> eventBookings = getCompletedBookingsByEvent(eventId);
        return eventBookings.stream()
                .map(Ticket::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
