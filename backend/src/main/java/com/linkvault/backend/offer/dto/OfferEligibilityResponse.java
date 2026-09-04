package com.linkvault.backend.offer.dto;

public class OfferEligibilityResponse {

    private Long offerId;
    private Long cardId;
    private boolean eligible;
    private String reason;

    public OfferEligibilityResponse() {
    }

    public OfferEligibilityResponse(
            Long offerId,
            Long cardId,
            boolean eligible,
            String reason) {

        this.offerId = offerId;
        this.cardId = cardId;
        this.eligible = eligible;
        this.reason = reason;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}