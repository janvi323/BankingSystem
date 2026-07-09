package com.bankingsystem.bankingsystem.Service;

import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.entity.LoanDecision;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import com.bankingsystem.bankingsystem.repository.LoanDecisionRepository;
import com.bankingsystem.bankingsystem.Service.LoanCalculationService;
import com.bankingsystem.bankingsystem.Service.EMIService;
import com.bankingsystem.bankingsystem.Service.ai.LoanDecisionEngine;
import com.bankingsystem.bankingsystem.Service.ai.FraudDetectionService;
import com.bankingsystem.bankingsystem.Service.ai.DynamicInterestRateEngine;
import com.bankingsystem.bankingsystem.event.LoanEventPublisher;
import com.bankingsystem.bankingsystem.entity.LoanEvent;
import com.bankingsystem.bankingsystem.repository.LoanEventRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * LoanService — orchestrates the full loan application lifecycle.
 *
 * <p>applyForLoan() now runs the complete AI pipeline:
 * <ol>
 *   <li>Fraud detection check</li>
 *   <li>Dynamic interest rate calculation</li>
 *   <li>EMI calculation</li>
 *   <li>Loan save (with status PENDING initially)</li>
 *   <li>AI Decision Engine evaluation</li>
 *   <li>Auto-approve or auto-reject based on score</li>
 *   <li>LoanDecision persistence</li>
 *   <li>Event publishing (full lifecycle via Spring ApplicationEvents)</li>
 * </ol>
 */
@Service
public class LoanService {

    private final LoanRepository         loanRepository;
    private final LoanDecisionRepository  decisionRepository;
    private final LoanEventRepository     eventRepository;
    private final CreditScoreClientService creditScoreClientService;
    private final LoanCalculationService  loanCalculationService;
    private final DynamicInterestRateEngine rateEngine;
    private final EMIService              emiService;
    private final LoanDecisionEngine      decisionEngine;
    private final FraudDetectionService   fraudService;
    private final LoanEventPublisher      eventPublisher;

    public LoanService(LoanRepository loanRepository,
                       LoanDecisionRepository decisionRepository,
                       LoanEventRepository eventRepository,
                       CreditScoreClientService creditScoreClientService,
                       LoanCalculationService loanCalculationService,
                       DynamicInterestRateEngine rateEngine,
                       EMIService emiService,
                       LoanDecisionEngine decisionEngine,
                       FraudDetectionService fraudService,
                       LoanEventPublisher eventPublisher) {
        this.loanRepository          = loanRepository;
        this.decisionRepository      = decisionRepository;
        this.eventRepository         = eventRepository;
        this.creditScoreClientService = creditScoreClientService;
        this.loanCalculationService  = loanCalculationService;
        this.rateEngine              = rateEngine;
        this.emiService              = emiService;
        this.decisionEngine          = decisionEngine;
        this.fraudService            = fraudService;
        this.eventPublisher          = eventPublisher;
    }

    // ── Core: Apply for Loan (AI Pipeline) ───────────────────────────────────

    /**
     * Full AI-powered loan application flow.
     *
     * @param customer        the applicant
     * @param amount          requested principal in INR
     * @param purpose         loan purpose
     * @param tenure          loan tenure in months
     * @param employmentType  SALARIED / SELF_EMPLOYED / BUSINESS / UNEMPLOYED
     * @param empYears        years at current employer
     * @param monthlyIncome   declared monthly gross income in INR
     * @param selectedBank    bank code chosen from Multi-Bank Comparison (nullable)
     * @return saved Loan entity
     */
    @Transactional
    public Loan applyForLoan(Customer customer, Double amount, String purpose,
                              Integer tenure, String employmentType, Integer empYears,
                              Double monthlyIncome, String selectedBank) throws Exception {
        if (amount  == null || amount  <= 0) throw new Exception("Loan amount must be greater than 0");
        if (tenure  == null || tenure  <= 0) throw new Exception("Loan tenure must be greater than 0");

        // ── Step 1: Fraud Detection ───────────────────────────────────────
        FraudDetectionService.FraudResult fraud =
                fraudService.check(customer.getId(), amount, monthlyIncome);

        // ── Step 2: Dynamic Interest Rate ─────────────────────────────────
        double interestRate = rateEngine.calculate(customer, purpose, amount, tenure, selectedBank);
        double emiAmount    = loanCalculationService.calculateEMI(amount, interestRate, tenure);
        double totalAmount  = loanCalculationService.calculateTotalAmount(emiAmount, tenure);

        // ── Step 3: Build and save Loan ───────────────────────────────────
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setAmount(amount);
        loan.setPurpose(purpose);
        loan.setTenure(tenure);
        loan.setInterestRate(interestRate);
        loan.setEmiAmount(emiAmount);
        loan.setTotalAmount(totalAmount);
        loan.setStatus(Loan.Status.PENDING);
        loan.setApplicationDate(LocalDateTime.now());
        loan.setEmploymentType(employmentType);
        loan.setEmploymentStabilityYears(empYears);
        loan.setMonthlyIncome(monthlyIncome);
        loan.setSelectedBankName(selectedBank);

        Loan savedLoan = loanRepository.save(loan);

        // ── Step 4: Publish LoanApplied event ─────────────────────────────
        eventPublisher.publishLoanApplied(savedLoan, customer);

        // ── Step 5: Publish CreditScore event ─────────────────────────────
        if (customer.getCreditScore() != null) {
            eventPublisher.publishCreditScoreGenerated(savedLoan, customer.getCreditScore());
        }

        // ── Step 6: Fraud check event ──────────────────────────────────────
        persistEvent(savedLoan.getId(), LoanEvent.EventType.FRAUD_CHECK_COMPLETED,
                fraud.flagged()
                    ? "⚠️ Fraud flag: " + fraud.reason()
                    : "✅ No fraud patterns detected",
                "flagged=" + fraud.flagged() +
                (fraud.reason() != null ? "; reason=" + fraud.reason() : ""));

        // ── Step 7: AI Decision Engine ────────────────────────────────────
        LoanDecision decision = decisionEngine.evaluate(customer, savedLoan);
        decision.setFraudFlagged(fraud.flagged());

        // ── Step 8: Auto-update loan status based on AI decision ──────────
        if (decision.getDecisionType() == LoanDecision.DecisionType.AUTO_APPROVED) {
            savedLoan.setStatus(Loan.Status.APPROVED);
            savedLoan.setApprovalDate(LocalDateTime.now());
            savedLoan.setAdminComments("Auto-approved by AI engine (score: " +
                    String.format("%.0f", decision.getDecisionScore()) + "/100)");
            loanRepository.save(savedLoan);
        } else if (decision.getDecisionType() == LoanDecision.DecisionType.AUTO_REJECTED) {
            savedLoan.setStatus(Loan.Status.REJECTED);
            savedLoan.setApprovalDate(LocalDateTime.now());
            savedLoan.setAdminComments("Auto-rejected by AI engine (score: " +
                    String.format("%.0f", decision.getDecisionScore()) + "/100)");
            loanRepository.save(savedLoan);
        }
        // MANUAL_REVIEW stays PENDING for admin

        // ── Step 9: Save LoanDecision ─────────────────────────────────────
        decision.setLoan(savedLoan);
        decisionRepository.save(decision);

        // ── Step 10: Publish Decision event ───────────────────────────────
        eventPublisher.publishDecisionCompleted(savedLoan, decision);

        // ── Step 11: Generate EMIs if approved ────────────────────────────
        if (savedLoan.getStatus() == Loan.Status.APPROVED) {
            try {
                emiService.generateEMIsForLoan(savedLoan);
                persistEvent(savedLoan.getId(), LoanEvent.EventType.EMI_GENERATED,
                        "EMI schedule generated (" + tenure + " installments)",
                        "emiAmount=₹" + emiAmount + "; tenure=" + tenure);
                eventPublisher.publishLoanApproved(savedLoan);
                eventPublisher.publishNotificationSent(savedLoan, "APPROVAL",
                        "Congratulations! Your loan of ₹" + amount + " has been approved.");
            } catch (Exception e) {
                System.err.println("EMI generation failed for loan " + savedLoan.getId() + ": " + e.getMessage());
            }
        } else if (savedLoan.getStatus() == Loan.Status.REJECTED) {
            eventPublisher.publishLoanRejected(savedLoan,
                    decision.getRejectionReasonsList().isEmpty()
                        ? "Score below threshold"
                        : decision.getRejectionReasonsList().get(0));
            eventPublisher.publishNotificationSent(savedLoan, "REJECTION",
                    "Your loan application was not approved. See recommendations to improve eligibility.");
        }

        // ── Step 12: Update credit score ──────────────────────────────────
        try {
            creditScoreClientService.updateCreditScoreForLoanStatus(
                    customer.getId(), savedLoan.getStatus().name(), amount);
        } catch (Exception e) {
            System.err.println("Credit score update failed: " + e.getMessage());
        }

        return savedLoan;
    }

    /**
     * Backward-compatible overload — used by existing WebController.
     * Defaults: SALARIED, 2 years, income from customer.income/12.
     */
    @Transactional
    public Loan applyForLoan(Customer customer, Double amount,
                              String purpose, Integer tenure) throws Exception {
        Double monthlyIncome = customer.getIncome() != null ? customer.getIncome() / 12.0 : null;
        return applyForLoan(customer, amount, purpose, tenure,
                "SALARIED", 2, monthlyIncome, null);
    }

    // ── Admin: Update Loan Status ─────────────────────────────────────────────

    @Transactional
    public Loan updateLoanStatus(Long loanId, Loan.Status status,
                                  String adminComments) throws Exception {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new Exception("Loan not found"));

        Loan.Status previousStatus = loan.getStatus();
        loan.setStatus(status);
        loan.setAdminComments(adminComments);

        if (status == Loan.Status.APPROVED || status == Loan.Status.REJECTED) {
            loan.setApprovalDate(LocalDateTime.now());
        }

        Loan savedLoan = loanRepository.save(loan);

        if (status == Loan.Status.APPROVED && previousStatus != Loan.Status.APPROVED) {
            try {
                emiService.generateEMIsForLoan(savedLoan);
            } catch (Exception e) {
                System.err.println("Error generating EMIs for loan " + loanId + ": " + e.getMessage());
            }
            eventPublisher.publishLoanApproved(savedLoan);
            eventPublisher.publishNotificationSent(savedLoan, "APPROVAL",
                    "Your loan has been approved by our team.");
        }

        if ((status == Loan.Status.APPROVED || status == Loan.Status.REJECTED)
                && previousStatus != status) {
            try {
                creditScoreClientService.updateCreditScoreForLoanStatus(
                        loan.getCustomer().getId(), status.toString(), loan.getAmount());
            } catch (Exception e) {
                System.err.println("Credit score update failed: " + e.getMessage());
            }
        }

        if (status == Loan.Status.REJECTED && previousStatus != Loan.Status.REJECTED) {
            eventPublisher.publishLoanRejected(savedLoan, adminComments);
        }

        return savedLoan;
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public List<Loan> getLoansByCustomerId(Long customerId) { return loanRepository.findByCustomerId(customerId); }
    public List<Loan> getAllLoans()                         { return loanRepository.findAll(); }
    public List<Loan> getLoansByStatus(Loan.Status status)  { return loanRepository.findByStatus(status); }
    public Optional<Loan> getLoanById(Long loanId)          { return loanRepository.findById(loanId); }

    public int getTotalLoanCount()                          { return (int) loanRepository.count(); }
    public int getPendingLoanCount()                        { return loanRepository.findByStatus(Loan.Status.PENDING).size(); }
    public int getCustomerLoanCount(Long id)                { return loanRepository.findByCustomerId(id).size(); }
    public int getCustomerPendingLoanCount(Long id)         { return loanRepository.findByCustomerIdAndStatus(id, Loan.Status.PENDING).size(); }
    public int getCustomerApprovedLoanCount(Long id)        { return loanRepository.findByCustomerIdAndStatus(id, Loan.Status.APPROVED).size(); }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void persistEvent(Long loanId, LoanEvent.EventType type,
                               String description, String data) {
        try {
            eventRepository.save(new LoanEvent(loanId, type, description, data));
        } catch (Exception e) {
            System.err.println("Event persistence failed: " + e.getMessage());
        }
    }
}
