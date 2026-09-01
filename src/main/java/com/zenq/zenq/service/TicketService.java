package com.zenq.zenq.service;

import com.zenq.zenq.entity.Ticket;
import com.zenq.zenq.exception.TicketNotFoundException;
import com.zenq.zenq.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Ticket getTicket(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found: " + id));
    }

    public List<Ticket> listTickets() {
        return ticketRepository.findAll();
    }

    public Ticket updateTicket(UUID id, Ticket updates) {
        Ticket existing = getTicket(id);

        existing.setSubject(updates.getSubject());
        existing.setDescription(updates.getDescription());
        existing.setChannel(updates.getChannel());
        existing.setStatus(updates.getStatus());
        existing.setCategory(updates.getCategory());
        existing.setPriority(updates.getPriority());
        existing.setClientId(updates.getClientId());
        existing.setProductId(updates.getProductId());
        existing.setSuggestedResponse(updates.getSuggestedResponse());
        existing.setAssignedAgent(updates.getAssignedAgent());
        existing.setResolutionType(updates.getResolutionType());

        return ticketRepository.save(existing);
    }

    public void deleteTicket(UUID id) {
        Ticket existing = getTicket(id);
        ticketRepository.delete(existing);
    }
}
