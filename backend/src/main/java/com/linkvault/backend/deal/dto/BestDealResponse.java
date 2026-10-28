package com.linkvault.backend.deal.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

public class BestDealResponse {

    private DealOptionResponse bestDeal;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<DealOptionResponse> alternatives;
    private String merchantName;
    private DealOfferScope offerScope;
    private BestDealNoMatchReason noDealReason;

    public BestDealResponse() {
    }

    public BestDealResponse(
            DealOptionResponse bestDeal,
            List<DealOptionResponse> alternatives) {

        this(bestDeal, alternatives, null, DealOfferScope.GENERAL_ONLY);
    }

    public BestDealResponse(
            DealOptionResponse bestDeal,
            List<DealOptionResponse> alternatives,
            String merchantName,
            DealOfferScope offerScope) {

        this(bestDeal, alternatives, merchantName, offerScope, null);
    }

    public BestDealResponse(
            DealOptionResponse bestDeal,
            List<DealOptionResponse> alternatives,
            String merchantName,
            DealOfferScope offerScope,
            BestDealNoMatchReason noDealReason) {

        this.bestDeal = bestDeal;
        this.alternatives = alternatives;
        this.merchantName = merchantName;
        this.offerScope = offerScope;
        this.noDealReason = noDealReason;
    }

    public DealOptionResponse getBestDeal() {
        return bestDeal;
    }

    public void setBestDeal(DealOptionResponse bestDeal) {
        this.bestDeal = bestDeal;
    }

    public List<DealOptionResponse> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<DealOptionResponse> alternatives) {
        this.alternatives = alternatives;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public DealOfferScope getOfferScope() {
        return offerScope;
    }

    public void setOfferScope(DealOfferScope offerScope) {
        this.offerScope = offerScope;
    }

    public BestDealNoMatchReason getNoDealReason() {
        return noDealReason;
    }

    public void setNoDealReason(BestDealNoMatchReason noDealReason) {
        this.noDealReason = noDealReason;
    }
}
