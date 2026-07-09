package com.bankingsystem.bankingsystem.event;

import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.entity.LoanDecision;
import com.bankingsystem.bankingsystem.entity.Customer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * LoanEventPublisher — thin facade over Spring's ApplicationEventPublisher.
 *
 * <p>All event publishing in the system goes through this class. The
 * {@code ApplicationEventPublisher} abstraction means that replacing this
 * with a Kafka producer later is a single-class change.
 *
 * <p>Interview note: "We use Spring Application Events for synchronous
 * in-process event propagation. The LoanEventPublisher facade allows us
 * to swap to Kafka/RabbitMQ by changing only this class."
 */
@Component
public class LoanEventPublisher {

    private final ApplicationEventPublisher publisher;

    public LoanEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishLoanApplied(Loan loan, Customer customer) {
        publisher.publishEvent(new LoanAppliedEvent(this, loan, customer));
    }

    public void publishCreditScoreGenerated(Loan loan, int creditScore) {
        publisher.publishEvent(new CreditScoreGeneratedEvent(this, loan, creditScore));
    }

    public void publishDecisionCompleted(Loan loan, LoanDecision decision) {
        publisher.publishEvent(new DecisionCompletedEvent(this, loan, decision));
    }

    public void publishLoanApproved(Loan loan) {
        publisher.publishEvent(new LoanApprovedEvent(this, loan));
    }

    public void publishLoanRejected(Loan loan, String reason) {
        publisher.publishEvent(new LoanRejectedEvent(this, loan, reason));
    }

    public void publishNotificationSent(Loan loan, String type, String message) {
        publisher.publishEvent(new NotificationSentEvent(this, loan, type, message));
    }
}
