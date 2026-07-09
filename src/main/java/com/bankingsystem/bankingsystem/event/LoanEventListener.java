package com.bankingsystem.bankingsystem.event;

import com.bankingsystem.bankingsystem.entity.LoanEvent;
import com.bankingsystem.bankingsystem.entity.LoanDecision;
import com.bankingsystem.bankingsystem.repository.LoanEventRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * LoanEventListener — handles all loan lifecycle events and persists them
 * as audit trail records in the {@code loan_event} table.
 *
 * <p>Each listener method:
 * <ol>
 *   <li>Logs the event to the application log</li>
 *   <li>Persists a {@link LoanEvent} record to the DB</li>
 *   <li>Performs any side effects (e.g. notification simulation)</li>
 * </ol>
 */
@Component
public class LoanEventListener {

    private static final Logger log = Logger.getLogger(LoanEventListener.class.getName());
    private final LoanEventRepository eventRepository;

    public LoanEventListener(LoanEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @EventListener
    public void onLoanApplied(LoanAppliedEvent event) {
        Long loanId = event.getLoan().getId();
        log.info("[EVENT] LoanApplied — loanId=" + loanId +
                 ", customer=" + event.getCustomer().getEmail() +
                 ", amount=₹" + event.getLoan().getAmount());

        persist(loanId, LoanEvent.EventType.LOAN_APPLIED,
                "Loan application submitted",
                "customer=" + event.getCustomer().getEmail() +
                "; amount=₹" + event.getLoan().getAmount() +
                "; tenure=" + event.getLoan().getTenure() + " months" +
                "; bank=" + nvl(event.getLoan().getSelectedBankName()));
    }

    @EventListener
    public void onCreditScoreGenerated(CreditScoreGeneratedEvent event) {
        Long loanId = event.getLoan().getId();
        log.info("[EVENT] CreditScoreGenerated — loanId=" + loanId +
                 ", score=" + event.getCreditScore());

        persist(loanId, LoanEvent.EventType.CREDIT_SCORE_GENERATED,
                "Credit score fetched: " + event.getCreditScore(),
                "creditScore=" + event.getCreditScore());
    }

    @EventListener
    public void onDecisionCompleted(DecisionCompletedEvent event) {
        Long loanId   = event.getLoan().getId();
        LoanDecision d = event.getDecision();
        log.info("[EVENT] DecisionCompleted — loanId=" + loanId +
                 ", decision=" + d.getDecisionType() +
                 ", score=" + d.getDecisionScore() +
                 ", confidence=" + d.getConfidencePercent() + "%" +
                 ", riskProfile=" + d.getRiskProfile());

        persist(loanId, LoanEvent.EventType.DECISION_COMPLETED,
                "AI decision: " + d.getDecisionType() + " (" + d.getConfidencePercent() + "% confidence)",
                "decision=" + d.getDecisionType() +
                "; score=" + d.getDecisionScore() +
                "; confidence=" + d.getConfidencePercent() + "%" +
                "; healthScore=" + d.getFinancialHealthScore() +
                "; riskProfile=" + d.getRiskProfile() +
                "; fraudFlagged=" + d.getFraudFlagged());
    }

    @EventListener
    public void onLoanApproved(LoanApprovedEvent event) {
        Long loanId = event.getLoan().getId();
        log.info("[EVENT] LoanApproved — loanId=" + loanId +
                 ", rate=" + event.getLoan().getInterestRate() + "%" +
                 ", emi=₹" + event.getLoan().getEmiAmount());

        persist(loanId, LoanEvent.EventType.LOAN_APPROVED,
                "Loan approved — EMIs being generated",
                "interestRate=" + event.getLoan().getInterestRate() + "%" +
                "; emiAmount=₹" + event.getLoan().getEmiAmount() +
                "; bank=" + nvl(event.getLoan().getSelectedBankName()));
    }

    @EventListener
    public void onLoanRejected(LoanRejectedEvent event) {
        Long loanId = event.getLoan().getId();
        log.info("[EVENT] LoanRejected — loanId=" + loanId +
                 ", reason=" + event.getReason());

        persist(loanId, LoanEvent.EventType.LOAN_REJECTED,
                "Loan rejected — see decision details",
                "reason=" + nvl(event.getReason()));
    }

    @EventListener
    public void onNotificationSent(NotificationSentEvent event) {
        Long loanId = event.getLoan().getId();
        log.info("[EVENT] NotificationSent — loanId=" + loanId +
                 ", type=" + event.getNotificationType());

        persist(loanId, LoanEvent.EventType.NOTIFICATION_SENT,
                "Notification sent: " + event.getNotificationType(),
                "type=" + event.getNotificationType() +
                "; message=" + nvl(event.getMessage()));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void persist(Long loanId, LoanEvent.EventType type,
                          String description, String eventData) {
        try {
            eventRepository.save(new LoanEvent(loanId, type, description, eventData));
        } catch (Exception ex) {
            // Never fail a transaction because of audit logging
            log.warning("[EVENT] Failed to persist event " + type + " for loan " + loanId + ": " + ex.getMessage());
        }
    }

    private String nvl(String s) { return s != null ? s : "N/A"; }
}
