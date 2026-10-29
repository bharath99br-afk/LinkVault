package com.linkvault.backend.offer.applicability.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.linkvault.backend.card.model.Card;
import com.linkvault.backend.offer.applicability.model.OfferBankApplicability;
import com.linkvault.backend.offer.applicability.model.OfferCardApplicability;

@Component
public class OfferCardMatcher {

    public boolean matches(
            Card card,
            List<OfferBankApplicability> bankApplicabilities,
            List<OfferCardApplicability> cardApplicabilities) {

        if (bankApplicabilities.isEmpty() && cardApplicabilities.isEmpty()) {
            return true;
        }

        Long cardBankId = card.getBank() == null ? null : card.getBank().getId();
        boolean bankMatches = cardBankId != null && bankApplicabilities.stream()
                .map(OfferBankApplicability::getBank)
                .filter(Objects::nonNull)
                .map(bank -> bank.getId())
                .anyMatch(bankId -> Objects.equals(cardBankId, bankId));

        Long cardProductId = card.getCardProduct() == null ? null : card.getCardProduct().getId();
        boolean cardProductMatches = cardProductId != null && cardApplicabilities.stream()
                .map(OfferCardApplicability::getCardProduct)
                .filter(Objects::nonNull)
                .map(cardProduct -> cardProduct.getId())
                .anyMatch(applicableProductId -> Objects.equals(cardProductId, applicableProductId));

        // V1 treats bank and catalog card-product restrictions as alternatives.
        return bankMatches || cardProductMatches;
    }
}
