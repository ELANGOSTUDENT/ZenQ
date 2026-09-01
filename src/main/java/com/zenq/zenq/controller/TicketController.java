package com.zenq.zenq.controller;

import com.zenq.zenq.dto.TicketRequest;
import com.zenq.zenq.dto.TicketResponse;
import com.zenq.zenq.dto.TicketUpdateRequest;
import com.zenq.zenq.entity.Ticket;
import com.zenq.zenq.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createTicket(@RequestBody TicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setSubject(request.subject());
        ticket.setDescription(request.description());
        ticket.setChannel(request.channel());

        Ticket saved = ticketService.createTicket(ticket);
        return TicketResponse.fromEntity(saved);
    }

    @GetMapping("/{id}")
    public TicketResponse getTicket(@PathVariable UUID id) {
        Ticket ticket = ticketService.getTicket(id);
        return TicketResponse.fromEntity(ticket);
    }

    @GetMapping
    public List<TicketResponse> listTickets() {
        return ticketService.listTickets().stream()
                .map(TicketResponse::fromEntity)
                .toList();
    }

    @PutMapping("/{id}")
    public TicketResponse updateTicket(@PathVariable UUID id, @RequestBody TicketUpdateRequest request) {
        Ticket updates = new Ticket();
        updates.setSubject(request.subject());
        updates.setDescription(request.description());
        updates.setChannel(request.channel());
        updates.setStatus(request.status());
        updates.setCategory(request.category());
        updates.setPriority(request.priority());
        updates.setClientId(request.clientId());
        updates.setProductId(request.productId());
        updates.setSuggestedResponse(request.suggestedResponse());
        updates.setAssignedAgent(request.assignedAgent());
        updates.setResolutionType(request.resolutionType());

        Ticket saved = ticketService.updateTicket(id, updates);
        return TicketResponse.fromEntity(saved);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
    }
}
