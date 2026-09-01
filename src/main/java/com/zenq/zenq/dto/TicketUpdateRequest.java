package com.zenq.zenq.dto;

import com.zenq.zenq.entity.TicketChannel;
import com.zenq.zenq.entity.TicketResolutionType;
import com.zenq.zenq.entity.TicketStatus;

import java.util.UUID;

public record TicketUpdateRequest(
        String subject,
        String description,
        TicketChannel channel,
        TicketStatus status,
        String category,
        String priority,
        UUID clientId,
        UUID productId,
        String suggestedResponse,
        String assignedAgent,
        TicketResolutionType resolutionType
) {
}
