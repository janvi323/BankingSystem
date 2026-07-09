package com.bankingsystem.bankingsystem.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PreApprovedOffersResult — full response containing all pre-approved offers for a customer.
 */
public class PreApprovedOffersResult {

    private List<PreApprovedOffer> offers;
    private int eligibleCount;
    private LocalDateTime generatedAt;

    public PreApprovedOffersResult(List<PreApprovedOffer> offers) {
        this.offers = offers;
        this.eligibleCount = (int) offers.stream().filter(PreApprovedOffer::isEligible).count();
        this.generatedAt = LocalDateTime.now();
    }

    public List<PreApprovedOffer> getOffers()           { return offers; }
    public void setOffers(List<PreApprovedOffer> offers) { this.offers = offers; }
    public int getEligibleCount()                        { return eligibleCount; }
    public void setEligibleCount(int eligibleCount)      { this.eligibleCount = eligibleCount; }
    public LocalDateTime getGeneratedAt()                { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt){ this.generatedAt = generatedAt; }
}
