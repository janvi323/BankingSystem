package com.bankingsystem.bankingsystem.event;

import com.bankingsystem.bankingsystem.entity.Loan;
import org.springframework.context.ApplicationEvent;

/** Published after a notification is sent to the customer (in-memory logging for now). */
public class NotificationSentEvent extends ApplicationEvent {
    private final Loan   loan;
    private final String notificationType; // APPROVAL / REJECTION / EMI_REMINDER
    private final String message;

    public NotificationSentEvent(Object source, Loan loan,
                                  String notificationType, String message) {
        super(source);
        this.loan             = loan;
        this.notificationType = notificationType;
        this.message          = message;
    }

    public Loan   getLoan()             { return loan; }
    public String getNotificationType() { return notificationType; }
    public String getMessage()          { return message; }
}
