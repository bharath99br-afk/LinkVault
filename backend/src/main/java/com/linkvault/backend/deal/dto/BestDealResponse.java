package com.linkvault.backend.deal.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

public class BestDealResponse {

    private DealOptionResponse bestDeal;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private List<DealOptionResponse> alternatives;

    public BestDealResponse() {
    }

    public BestDealResponse(
            DealOptionResponse bestDeal,
            List<DealOptionResponse> alternatives) {

        this.bestDeal = bestDeal;
        this.alternatives = alternatives;
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
}