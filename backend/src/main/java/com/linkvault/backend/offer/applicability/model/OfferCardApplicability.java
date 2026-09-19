package com.linkvault.backend.offer.applicability.model;

import com.linkvault.backend.bank.model.Bank;
import com.linkvault.backend.card.catalog.model.CardProduct;
import com.linkvault.backend.offer.model.Offer;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "offer_card_applicability")
public class OfferCardApplicability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    /*
     * Retained temporarily for backward compatibility and
     * migration safety. The canonical source of card identity
     * is cardProduct.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_product_id", nullable = false)
    private CardProduct cardProduct;

    /*
     * Legacy field retained temporarily.
     * New application logic must not use this field for identity.
     */
    private String cardName;

    public OfferCardApplicability() {
    }

    public OfferCardApplicability(
            Long id,
            Offer offer,
            Bank bank,
            CardProduct cardProduct,
            String cardName) {

        this.id = id;
        this.offer = offer;
        this.bank = bank;
        this.cardProduct = cardProduct;
        this.cardName = cardName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public CardProduct getCardProduct() {
        return cardProduct;
    }

    public void setCardProduct(CardProduct cardProduct) {
        this.cardProduct = cardProduct;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
}