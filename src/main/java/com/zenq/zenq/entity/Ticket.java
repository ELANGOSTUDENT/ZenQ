package com.zenq.zenq.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String subject;

    public UUID getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Column(columnDefinition = "TEXT")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Enumerated(EnumType.STRING)
    private TicketChannel channel;

    public TicketChannel getChannel() {
        return channel;
    }

    public void setChannel(TicketChannel channel) {
        this.channel = channel;
    }

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String priority;

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    private UUID clientId;

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    private UUID productId;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    @Column(columnDefinition = "TEXT")
    private String suggestedResponse;

    public String getSuggestedResponse() {
        return suggestedResponse;
    }

    public void setSuggestedResponse(String suggestedResponse) {
        this.suggestedResponse = suggestedResponse;
    }

    private String assignedAgent;

    public String getAssignedAgent() {
        return assignedAgent;
    }

    public void setAssignedAgent(String assignedAgent) {
        this.assignedAgent = assignedAgent;
    }

    @Enumerated(EnumType.STRING)
    private TicketResolutionType resolutionType;

    public TicketResolutionType getResolutionType() {
        return resolutionType;
    }

    public void setResolutionType(TicketResolutionType resolutionType) {
        this.resolutionType = resolutionType;
    }

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(columnDefinition = "vector(1536)")
    private float[] embedding;

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }

}
