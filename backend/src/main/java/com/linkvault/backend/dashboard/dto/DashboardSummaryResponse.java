package com.linkvault.backend.dashboard.dto;

public class DashboardSummaryResponse {

    private Long userId;
    private String userName;

    private long totalLinks;
    private long totalProducts;
    private long totalMerchants;
    private long totalCards;
    private long activeOffers;

    public DashboardSummaryResponse() {
    }

    public DashboardSummaryResponse(
            Long userId,
            String userName,
            long totalLinks,
            long totalProducts,
            long totalMerchants,
            long totalCards,
            long activeOffers) {

        this.userId = userId;
        this.userName = userName;
        this.totalLinks = totalLinks;
        this.totalProducts = totalProducts;
        this.totalMerchants = totalMerchants;
        this.totalCards = totalCards;
        this.activeOffers = activeOffers;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTotalLinks() {
        return totalLinks;
    }

    public void setTotalLinks(long totalLinks) {
        this.totalLinks = totalLinks;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalMerchants() {
        return totalMerchants;
    }

    public void setTotalMerchants(long totalMerchants) {
        this.totalMerchants = totalMerchants;
    }

    public long getTotalCards() {
        return totalCards;
    }

    public void setTotalCards(long totalCards) {
        this.totalCards = totalCards;
    }

    public long getActiveOffers() {
        return activeOffers;
    }

    public void setActiveOffers(long activeOffers) {
        this.activeOffers = activeOffers;
    }
}