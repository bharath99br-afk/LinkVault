package com.linkvault.backend.card.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CardRequest {

    @NotBlank(message = "Card name cannot be empty")
    @Size(min = 2, max = 100, message = "Card name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Last four digits cannot be empty")
    @Size(min = 4, max = 4, message = "Last four digits must contain exactly 4 characters")
    private String lastFourDigits;

    @NotBlank(message = "Card type cannot be empty")
    @Size(max = 50, message = "Card type cannot exceed 50 characters")
    private String cardType;

    @NotNull(message = "Bank ID cannot be null")
    private Long bankId;

    public CardRequest() {
    }

    public CardRequest(
            String name,
            String lastFourDigits,
            String cardType,
            Long bankId) {

        this.name = name;
        this.lastFourDigits = lastFourDigits;
        this.cardType = cardType;
        this.bankId = bankId;
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
}