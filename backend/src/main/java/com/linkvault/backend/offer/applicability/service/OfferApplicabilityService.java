package com.linkvault.backend.offer.applicability.service;

import com.linkvault.backend.bank.model.Bank;
import com.linkvault.backend.bank.repository.BankRepository;
import com.linkvault.backend.exception.DuplicateResourceException;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.offer.applicability.dto.OfferBankApplicabilityRequest;
import com.linkvault.backend.offer.applicability.dto.OfferCardApplicabilityRequest;
import com.linkvault.backend.offer.applicability.model.OfferBankApplicability;
import com.linkvault.backend.offer.applicability.model.OfferCardApplicability;
import com.linkvault.backend.offer.applicability.repository.OfferBankApplicabilityRepository;
import com.linkvault.backend.offer.applicability.repository.OfferCardApplicabilityRepository;
import com.linkvault.backend.offer.model.Offer;
import com.linkvault.backend.offer.repository.OfferRepository;

import org.springframework.stereotype.Service;

@Service
public class OfferApplicabilityService {

    private final OfferRepository offerRepository;
    private final BankRepository bankRepository;
    private final OfferBankApplicabilityRepository offerBankRepository;
    private final OfferCardApplicabilityRepository offerCardRepository;

    public OfferApplicabilityService(
            OfferRepository offerRepository,
            BankRepository bankRepository,
            OfferBankApplicabilityRepository offerBankRepository,
            OfferCardApplicabilityRepository offerCardRepository) {

        this.offerRepository = offerRepository;
        this.bankRepository = bankRepository;
        this.offerBankRepository = offerBankRepository;
        this.offerCardRepository = offerCardRepository;
    }

    public void addBankApplicability(
            Long offerId,
            OfferBankApplicabilityRequest request) {

        Offer offer = getOffer(offerId);

        Bank bank = getBank(request.getBankId());

        if (offerBankRepository.existsByOfferIdAndBankId(
                offerId,
                bank.getId())) {

            throw new DuplicateResourceException(
                    "Offer is already applicable to this bank");
        }

        OfferBankApplicability applicability = new OfferBankApplicability();

        applicability.setOffer(offer);
        applicability.setBank(bank);

        offerBankRepository.save(applicability);
    }

    public void addCardApplicability(
            Long offerId,
            OfferCardApplicabilityRequest request) {

        Offer offer = getOffer(offerId);

        Bank bank = getBank(request.getBankId());

        if (offerCardRepository
                .existsByOfferIdAndBankIdAndCardNameIgnoreCase(
                        offerId,
                        bank.getId(),
                        request.getCardName())) {

            throw new DuplicateResourceException(
                    "Offer is already applicable to this card");
        }

        OfferCardApplicability applicability = new OfferCardApplicability();

        applicability.setOffer(offer);
        applicability.setBank(bank);
        applicability.setCardName(request.getCardName());

        offerCardRepository.save(applicability);
    }

    public void removeBankApplicability(
            Long offerId,
            Long bankId) {

        OfferBankApplicability applicability = offerBankRepository.findByOfferId(offerId)
                .stream()
                .filter(item -> item.getBank().getId().equals(bankId))
                .findFirst()
                .orElseThrow(() -> new LinkNotFoundException(
                        "Offer bank applicability not found"));

        offerBankRepository.delete(applicability);
    }

    public void removeCardApplicability(
            Long offerId,
            Long bankId,
            String cardName) {

        OfferCardApplicability applicability = offerCardRepository.findByOfferId(offerId)
                .stream()
                .filter(item -> item.getBank().getId().equals(bankId)
                        && item.getCardName()
                                .equalsIgnoreCase(cardName))
                .findFirst()
                .orElseThrow(() -> new LinkNotFoundException(
                        "Offer card applicability not found"));

        offerCardRepository.delete(applicability);
    }

    private Offer getOffer(Long offerId) {

        return offerRepository.findById(offerId)
                .orElseThrow(() -> new LinkNotFoundException(
                        "Offer Not Found"));
    }

    private Bank getBank(Long bankId) {

        return bankRepository.findById(bankId)
                .orElseThrow(() -> new LinkNotFoundException(
                        "Bank Not Found"));
    }
}