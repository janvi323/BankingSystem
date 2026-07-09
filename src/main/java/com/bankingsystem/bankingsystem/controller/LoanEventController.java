package com.bankingsystem.bankingsystem.controller;

import com.bankingsystem.bankingsystem.entity.LoanEvent;
import com.bankingsystem.bankingsystem.repository.LoanEventRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * LoanEventController — exposes the event audit trail for loan timelines.
 */
@RestController
@RequestMapping("/api/loans")
public class LoanEventController {

    private final LoanEventRepository eventRepository;

    public LoanEventController(LoanEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * GET /api/loans/{loanId}/timeline
     * Returns the full event timeline for a loan, ordered chronologically.
     */
    @GetMapping("/{loanId}/timeline")
    public ResponseEntity<?> getTimeline(@PathVariable Long loanId) {
        try {
            List<LoanEvent> events = eventRepository.findByLoanIdOrderByOccurredAtAsc(loanId);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
