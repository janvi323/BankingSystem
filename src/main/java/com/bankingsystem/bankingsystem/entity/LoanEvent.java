package com.bankingsystem.bankingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * LoanEvent — audit trail record for the event-driven architecture.
 *
 * <p>Every significant step in a loan's lifecycle is recorded here via
 * {@code LoanEventPublisher}. The full timeline can be retrieved for any
 * loan via {@code GET /api/loans/{loanId}/timeline}.
 *
 * <p>eventData stores a human-readable summary of the event payload
 * (no JSON library required — plain key=value pairs separated by "; ").
 */
@Entity
@Table(name = "loan_event", indexes = {
    @Index(name = "idx_loan_event_loan_id", columnList = "loan_id"),
    @Index(name = "idx_loan_event_occurred_at", columnList = "occurred_at")
})
public class LoanEvent {

    public enum EventType {
        LOAN_APPLIED,
        CREDIT_SCORE_GENERATED,
        FRAUD_CHECK_COMPLETED,
        DECISION_COMPLETED,
        LOAN_APPROVED,
        LOAN_REJECTED,
        EMI_GENERATED,
        NOTIFICATION_SENT,
        BANK_SELECTED,
        SIMULATION_RUN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_id", nullable = false)
    private Long loanId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    /** Human-readable event summary, e.g. "decision=AUTO_APPROVED; score=78; confidence=78%" */
    @Column(columnDefinition = "TEXT")
    private String eventData;

    /** Short description shown in the timeline UI. */
    private String description;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt = LocalDateTime.now();

    // ── Constructors ──────────────────────────────────────────────────────────

    public LoanEvent() {}

    public LoanEvent(Long loanId, EventType eventType, String description, String eventData) {
        this.loanId      = loanId;
        this.eventType   = eventType;
        this.description = description;
        this.eventData   = eventData;
        this.occurredAt  = LocalDateTime.now();
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public String getEventData() { return eventData; }
    public void setEventData(String eventData) { this.eventData = eventData; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }

    // ── Helper ────────────────────────────────────────────────────────────────

    /** Icon class for the timeline UI based on event type. */
    public String getIconClass() {
        return switch (eventType) {
            case LOAN_APPLIED           -> "bi-file-earmark-plus text-primary";
            case CREDIT_SCORE_GENERATED -> "bi-graph-up text-info";
            case FRAUD_CHECK_COMPLETED  -> "bi-shield-check text-warning";
            case DECISION_COMPLETED     -> "bi-cpu text-purple";
            case LOAN_APPROVED          -> "bi-check-circle-fill text-success";
            case LOAN_REJECTED          -> "bi-x-circle-fill text-danger";
            case EMI_GENERATED          -> "bi-calendar2-check text-success";
            case NOTIFICATION_SENT      -> "bi-bell-fill text-secondary";
            case BANK_SELECTED          -> "bi-bank text-primary";
            case SIMULATION_RUN         -> "bi-play-circle text-info";
        };
    }
}
