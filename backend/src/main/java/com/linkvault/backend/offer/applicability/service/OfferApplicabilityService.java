package com.linkvault.backend.offer.applicability.service;

import com.linkvault.backend.bank.model.Bank;
import com.linkvault.backend.bank.repository.BankRepository;
import com.linkvault.backend.card.catalog.model.CardProduct;
import com.linkvault.backend.card.catalog.repository.CardProductRepository;
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
import org.springframework.transaction.annotation.Transactional;
import com.linkvault.backend.offer.applicability.dto.OfferCardApplicabilityResponse;
import com.linkvault.backend.offer.applicability.dto.OfferBankApplicabilityResponse;

@Service
public class OfferApplicabilityService {

        private final OfferRepository offerRepository;
        private final BankRepository bankRepository;
        private final CardProductRepository cardProductRepository;
        private final OfferBankApplicabilityRepository offerBankRepository;
        private final OfferCardApplicabilityRepository offerCardRepository;

        public OfferApplicabilityService(
                        OfferRepository offerRepository,
                        BankRepository bankRepository,
                        CardProductRepository cardProductRepository,
                        OfferBankApplicabilityRepository offerBankRepository,
                        OfferCardApplicabilityRepository offerCardRepository) {

                this.offerRepository = offerRepository;
                this.bankRepository = bankRepository;
                this.cardProductRepository = cardProductRepository;
                this.offerBankRepository = offerBankRepository;
                this.offerCardRepository = offerCardRepository;
        }

        @Transactional
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

        @Transactional
        public void addCardApplicability(
                        Long offerId,
                        OfferCardApplicabilityRequest request) {

                Offer offer = getOffer(offerId);

                CardProduct cardProduct = cardProductRepository
                                .findByIdAndActiveTrue(request.getCardProductId())
                                .orElseThrow(() -> new LinkNotFoundException(
                                                "Card Product Not Found"));

                if (offerCardRepository.existsByOfferIdAndCardProductId(
                                offerId,
                                cardProduct.getId())) {

                        throw new DuplicateResourceException(
                                        "Offer is already applicable to this card product");
                }

                OfferCardApplicability applicability = new OfferCardApplicability();

                applicability.setOffer(offer);
                applicability.setCardProduct(cardProduct);

                /*
                 * Keep legacy fields synchronized during the transition.
                 */
                applicability.setBank(cardProduct.getBank());
                applicability.setCardName(cardProduct.getName());

                offerCardRepository.save(applicability);
        }

        @Transactional
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

        @Transactional
        public void removeCardApplicability(
                        Long offerId,
                        Long cardProductId) {

                OfferCardApplicability applicability = offerCardRepository.findByOfferId(offerId)
                                .stream()
                                .filter(item -> item.getCardProduct().getId()
                                                .equals(cardProductId))
                                .findFirst()
                                .orElseThrow(() -> new LinkNotFoundException(
                                                "Offer card applicability not found"));

                offerCardRepository.delete(applicability);
        }

        @Transactional(readOnly = true)
        public java.util.List<OfferBankApplicabilityResponse> getBankApplicability(
                        Long offerId) {

                getOffer(offerId);

                return offerBankRepository.findByOfferId(offerId)
                                .stream()
                                .map(item -> new OfferBankApplicabilityResponse(
                                                item.getId(),
                                                item.getOffer().getId(),
                                                item.getBank().getId(),
                                                item.getBank().getName()))
                                .toList();
        }

        @Transactional(readOnly = true)
        public java.util.List<OfferCardApplicabilityResponse> getCardApplicability(
                        Long offerId) {

                getOffer(offerId);

                return offerCardRepository.findByOfferId(offerId)
                                .stream()
                                .map(item -> new OfferCardApplicabilityResponse(
                                                item.getId(),
                                                item.getOffer().getId(),
                                                item.getBank().getId(),
                                                item.getBank().getName(),
                                                item.getCardProduct().getId(),
                                                item.getCardProduct().getName()))
                                .toList();
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