package com.bankingsystem.bankingsystem.Service.ai;

import com.bankingsystem.bankingsystem.dto.SimulationRequest;
import com.bankingsystem.bankingsystem.dto.SimulationResult;
import com.bankingsystem.bankingsystem.entity.Customer;
import com.bankingsystem.bankingsystem.entity.Loan;
import com.bankingsystem.bankingsystem.entity.LoanDecision;
import com.bankingsystem.bankingsystem.repository.CustomerRepository;
import com.bankingsystem.bankingsystem.repository.LoanRepository;
import com.bankingsystem.bankingsystem.Service.LoanCalculationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * LoanSimulationService — computes what-if scenarios for loan eligibility.
 *
 * <p>Allows customers to ask:
 * <ul>
 *   <li>"What if my credit score is 50 points higher?"</li>
 *   <li>"What if I reduce my debt by ₹2L?"</li>
 *   <li>"What if my income increases to ₹80K/month?"</li>
 *   <li>"What if I apply for ₹3L instead of ₹5L?"</li>
 * </ul>
 *
 * <p>Works by creating a temporary in-memory copy of the customer profile,
 * applying the delta, and re-running the decision engine — no DB writes.
 */
@Service
public class LoanSimulationService {

    private final CustomerRepository customerRepository;
    private final LoanRepository     loanRepository;
    private final LoanDecisionEngine decisionEngine;
    private final LoanCalculationService calcService;

    public LoanSimulationService(CustomerRepository customerRepository,
                                 LoanRepository loanRepository,
                                 LoanDecisionEngine decisionEngine,
                                 LoanCalculationService calcService) {
        this.customerRepository = customerRepository;
        this.loanRepository     = loanRepository;
        this.decisionEngine     = decisionEngine;
        this.calcService        = calcService;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public SimulationResult simulate(SimulationRequest req) {
        Customer original = customerRepository.findById(req.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        List<Loan> existingLoans = loanRepository.findByCustomerId(req.getCustomerId());

        // ── Current state ──────────────────────────────────────────────────
        Loan currentLoan = buildDummyLoan(original, req.getLoanAmount(), req.getTenure(),
                req.getPurpose(), null, 0);
        double currentRate = calcService.calculateInterestRate(req.getPurpose(),
                req.getLoanAmount(), req.getTenure());
        currentLoan.setInterestRate(currentRate);
        currentLoan.setEmiAmount(calcService.calculateEMI(req.getLoanAmount(), currentRate, req.getTenure()));

        LoanDecision currentDecision = decisionEngine.evaluate(original, currentLoan);

        // ── Simulated state ────────────────────────────────────────────────
        Customer simCustomer = shallowCopy(original);
        applyDeltas(simCustomer, req);

        double simAmount  = req.getNewLoanAmount() != null ? req.getNewLoanAmount() : req.getLoanAmount();
        int    simTenure  = req.getNewTenure()    != null ? req.getNewTenure()    : req.getTenure();
        Loan   simLoan    = buildDummyLoan(simCustomer, simAmount, simTenure,
                req.getPurpose(), null, 0);
        double simRate    = calcService.calculateInterestRate(req.getPurpose(), simAmount, simTenure);
        simLoan.setInterestRate(simRate);
        simLoan.setEmiAmount(calcService.calculateEMI(simAmount, simRate, simTenure));

        LoanDecision simDecision = decisionEngine.evaluate(simCustomer, simLoan);

        // ── Build result ───────────────────────────────────────────────────
        return buildResult(req, currentDecision, simDecision,
                currentRate, simRate,
                currentLoan.getEmiAmount(), simLoan.getEmiAmount());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void applyDeltas(Customer c, SimulationRequest req) {
        if (req.getCreditScoreDelta() != null && c.getCreditScore() != null) {
            c.setCreditScore(c.getCreditScore() + req.getCreditScoreDelta());
        }
        if (req.getDebtReductionAmount() != null && c.getIncome() != null) {
            // Reduce DTI proportionally
            double income = c.getIncome();
            if (income > 0 && c.getDebtToIncomeRatio() != null) {
                double currentDebt = c.getDebtToIncomeRatio() * income;
                double newDebt     = Math.max(0, currentDebt - req.getDebtReductionAmount());
                c.setDebtToIncomeRatio(newDebt / income);
            }
        }
        if (req.getNewMonthlyIncome() != null) {
            c.setIncome(req.getNewMonthlyIncome() * 12);
        }
    }

    private Loan buildDummyLoan(Customer c, double amount, int tenure,
                                 String purpose, String bankCode, int empYears) {
        Loan l = new Loan();
        l.setCustomer(c);
        l.setAmount(amount);
        l.setTenure(tenure);
        l.setPurpose(purpose);
        l.setSelectedBankName(bankCode);
        l.setEmploymentStabilityYears(empYears > 0 ? empYears : 2);
        l.setMonthlyIncome(c.getIncome() != null ? c.getIncome() / 12.0 : null);
        l.setStatus(Loan.Status.PENDING);
        return l;
    }

    /** Shallow copy — creates a new Customer with the same field values. */
    private Customer shallowCopy(Customer src) {
        Customer c = new Customer();
        c.setId(src.getId());
        c.setCreditScore(src.getCreditScore());
        c.setDebtToIncomeRatio(src.getDebtToIncomeRatio());
        c.setPaymentHistoryScore(src.getPaymentHistoryScore());
        c.setCreditUtilizationRatio(src.getCreditUtilizationRatio());
        c.setCreditAgeMonths(src.getCreditAgeMonths());
        c.setNumberOfAccounts(src.getNumberOfAccounts());
        c.setEmi(src.getEmi());
        c.setIncome(src.getIncome());
        return c;
    }

    private SimulationResult buildResult(SimulationRequest req,
                                          LoanDecision curr, LoanDecision sim,
                                          double currRate, double simRate,
                                          double currEmi, double simEmi) {
        SimulationResult r = new SimulationResult();
        r.setScenarioDescription(buildScenarioDesc(req));
        r.setCurrentDecision(curr.getDecisionType().name());
        r.setSimulatedDecision(sim.getDecisionType().name());
        r.setCurrentApprovalProb(curr.getConfidencePercent() != null ? curr.getConfidencePercent() : 0);
        r.setSimulatedApprovalProb(sim.getConfidencePercent() != null ? sim.getConfidencePercent() : 0);
        r.setCurrentInterestRate(currRate);
        r.setSimulatedInterestRate(simRate);
        r.setCurrentEmi(currEmi);
        r.setSimulatedEmi(simEmi);
        r.setCurrentFinancialHealth(curr.getFinancialHealthScore() != null ? curr.getFinancialHealthScore() : 0);
        r.setSimulatedFinancialHealth(sim.getFinancialHealthScore() != null ? sim.getFinancialHealthScore() : 0);
        r.setBecomeEligible(
            curr.getDecisionType() != LoanDecision.DecisionType.AUTO_APPROVED &&
            sim.getDecisionType()  == LoanDecision.DecisionType.AUTO_APPROVED
        );
        int probDelta = r.getSimulatedApprovalProb() - r.getCurrentApprovalProb();
        r.setImpactSummary(buildImpactSummary(probDelta, r.isBecomeEligible(),
                currRate - simRate, currEmi - simEmi));
        return r;
    }

    private String buildScenarioDesc(SimulationRequest req) {
        StringBuilder sb = new StringBuilder("What if ");
        if (req.getCreditScoreDelta() != null)
            sb.append("credit score increases by ").append(req.getCreditScoreDelta()).append(" points; ");
        if (req.getDebtReductionAmount() != null)
            sb.append("debt reduces by ₹").append(String.format("%.0f", req.getDebtReductionAmount())).append("; ");
        if (req.getNewMonthlyIncome() != null)
            sb.append("monthly income becomes ₹").append(String.format("%.0f", req.getNewMonthlyIncome())).append("; ");
        if (req.getNewLoanAmount() != null)
            sb.append("loan amount is ₹").append(String.format("%.0f", req.getNewLoanAmount())).append("; ");
        String desc = sb.toString().replaceAll("; $", "?");
        return desc.equals("What if ?") ? "Baseline simulation" : desc;
    }

    private String buildImpactSummary(int probDelta, boolean becomeEligible,
                                       double rateDelta, double emiDelta) {
        if (becomeEligible) {
            return String.format("✅ This change makes you eligible! Approval probability improves by +%d%%.", probDelta);
        }
        if (probDelta > 0) {
            return String.format("📈 Approval probability improves by +%d%%. Rate saves ₹%.1f%% p.a.", probDelta, rateDelta);
        }
        if (probDelta == 0) {
            return "⚖️ This change has minimal impact on approval probability.";
        }
        return String.format("📉 This scenario reduces approval probability by %d%%.", Math.abs(probDelta));
    }
}
