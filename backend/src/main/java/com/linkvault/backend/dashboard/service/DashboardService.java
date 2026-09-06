package com.linkvault.backend.dashboard.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linkvault.backend.card.repository.CardRepository;
import com.linkvault.backend.dashboard.dto.DashboardSummaryResponse;
import com.linkvault.backend.link.repository.LinkRepository;
import com.linkvault.backend.merchant.repository.MerchantRepository;
import com.linkvault.backend.offer.repository.OfferRepository;
import com.linkvault.backend.product.repository.ProductRepository;
import com.linkvault.backend.security.CurrentUserService;
import com.linkvault.backend.user.model.User;

@Service
public class DashboardService {

    private final LinkRepository linkRepository;
    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;
    private final CardRepository cardRepository;
    private final OfferRepository offerRepository;
    private final CurrentUserService currentUserService;

    public DashboardService(
            LinkRepository linkRepository,
            ProductRepository productRepository,
            MerchantRepository merchantRepository,
            CardRepository cardRepository,
            OfferRepository offerRepository,
            CurrentUserService currentUserService) {

        this.linkRepository = linkRepository;
        this.productRepository = productRepository;
        this.merchantRepository = merchantRepository;
        this.cardRepository = cardRepository;
        this.offerRepository = offerRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {

        User currentUser = currentUserService.getCurrentUser();

        Long userId = currentUser.getId();

        LocalDate today = LocalDate.now();

        long totalLinks = linkRepository.countByUserId(userId);

        long totalProducts = productRepository.countByUserId(userId);

        long totalMerchants = merchantRepository.countByUserId(userId);

        long totalCards = cardRepository.countByUserId(userId);

        long activeOffers = offerRepository
                .countByStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        today,
                        today);

        return new DashboardSummaryResponse(
                userId,
                currentUser.getName(),
                totalLinks,
                totalProducts,
                totalMerchants,
                totalCards,
                activeOffers);
    }
}