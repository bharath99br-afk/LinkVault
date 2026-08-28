package com.linkvault.backend.card.model;

import com.linkvault.backend.bank.model.Bank;
import com.linkvault.backend.user.model.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Card name cannot be empty")
    @Size(min = 2, max = 100, message = "Card name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Last four digits cannot be empty")
    @Size(min = 4, max = 4, message = "Last four digits must contain exactly 4 characters")
    private String lastFourDigits;

    @NotBlank(message = "Card type cannot be empty")
    @Size(max = 50, message = "Card type cannot exceed 50 characters")
    private String cardType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Card() {
    }

    public Card(
            Long id,
            String name,
            String lastFourDigits,
            String cardType) {

        this.id = id;
        this.name = name;
        this.lastFourDigits = lastFourDigits;
        this.cardType = cardType;
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

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}