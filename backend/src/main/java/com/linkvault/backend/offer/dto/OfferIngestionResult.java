package com.linkvault.backend.offer.dto;

public class OfferIngestionResult {

    private OfferResponse offer;
    private boolean refreshed;

    public OfferIngestionResult() {
    }

    public OfferIngestionResult(OfferResponse offer, boolean refreshed) {
        this.offer = offer;
        this.refreshed = refreshed;
    }

    public OfferResponse getOffer() {
        return offer;
    }

    public void setOffer(OfferResponse offer) {
        this.offer = offer;
    }

    public boolean isRefreshed() {
        return refreshed;
    }

    public void setRefreshed(boolean refreshed) {
        this.refreshed = refreshed;
    }
}