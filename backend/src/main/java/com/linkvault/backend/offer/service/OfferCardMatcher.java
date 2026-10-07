package com.linkvault.backend.offer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.linkvault.backend.card.model.Card;
import com.linkvault.backend.offer.applicability.model.OfferBankApplicability;
import com.linkvault.backend.offer.applicability.model.OfferCardApplicability;

@Service
public class OfferCardMatcher {

    public boolean matches(
            Card card,
            List<OfferBankApplicability> bankApplicabilities,
            List<OfferCardApplicability> cardApplicabilities) {

        if (bankApplicabilities.isEmpty() && cardApplicabilities.isEmpty()) {
            return true;
        }

        boolean bankMatches = card.getBank() != null
                && bankApplicabilities.stream()
                        .anyMatch(applicability -> applicability.getBank().getId()
                                .equals(card.getBank().getId()));

        boolean cardProductMatches = card.getCardProduct() != null
                && cardApplicabilities.stream()
                        .anyMatch(applicability -> applicability.getCardProduct().getId()
                                .equals(card.getCardProduct().getId()));

        return bankMatches || cardProductMatches;
    }
}