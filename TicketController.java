package com.ticket.controller;

import com.ticket.model.Ticket;
import com.ticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    // Get all tickets of a user
    @GetMapping("/user/{username}")
    public List<Ticket> getTicketsByUser(@PathVariable String username) {
        return ticketRepository.findByUsername(username);
    }

    // Add new ticket
    @PostMapping
    public Ticket addTicket(@RequestBody Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    // Update ticket
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable String id, @RequestBody Ticket ticketDetails) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        if (optionalTicket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Ticket ticket = optionalTicket.get();
        ticket.setTrainName(ticketDetails.getTrainName());
        ticket.setSource(ticketDetails.getSource());
        ticket.setDestination(ticketDetails.getDestination());

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ResponseEntity.ok(updatedTicket);
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookTicket(@RequestBody Ticket ticketRequest) {
        Ticket ticket = new Ticket();
        ticket.setUsername(ticketRequest.getUsername());
        ticket.setSeat(ticketRequest.getSeat());

        // NEW: set price (fixed example, ya calculate dynamically)
        ticket.setPrice(200.0); // Example fixed price

        ticketRepository.save(ticket);
        return ResponseEntity.ok("Ticket booked with price: " + ticket.getPrice());
    }


    // Delete ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable String id) {
        if (!ticketRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        ticketRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Optional: get all tickets (for testing)
    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}
