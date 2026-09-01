package com.zenq.zenq.dto;

import com.zenq.zenq.entity.TicketChannel;

public record TicketRequest(
        String subject,
        String description,
        TicketChannel channel
) {
}
