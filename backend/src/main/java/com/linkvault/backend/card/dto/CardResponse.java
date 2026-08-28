package com.linkvault.backend.card.dto;

public class CardResponse {

    private Long id;
    private String name;
    private String lastFourDigits;
    private String cardType;
    private Long bankId;
    private String bankName;

    public CardResponse() {
    }

    public CardResponse(
            Long id,
            String name,
            String lastFourDigits,
            String cardType,
            Long bankId,
            String bankName) {

        this.id = id;
        this.name = name;
        this.lastFourDigits = lastFourDigits;
        this.cardType = cardType;
        this.bankId = bankId;
        this.bankName = bankName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}