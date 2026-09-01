package com.zenq.zenq.dto;

import com.zenq.zenq.entity.Ticket;
import com.zenq.zenq.entity.TicketChannel;
import com.zenq.zenq.entity.TicketStatus;

import java.time.Instant;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        String subject,
        String description,
        TicketChannel channel,
        TicketStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static TicketResponse fromEntity(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getChannel(),
                ticket.getStatus(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}
