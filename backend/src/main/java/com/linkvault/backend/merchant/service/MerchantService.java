package com.linkvault.backend.merchant.service;

import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.exception.DuplicateResourceException;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.globalmerchant.model.GlobalMerchant;
import com.linkvault.backend.globalmerchant.repository.GlobalMerchantRepository;
import com.linkvault.backend.globalmerchant.service.GlobalMerchantMatcher;
import com.linkvault.backend.merchant.dto.MerchantRequest;
import com.linkvault.backend.merchant.dto.MerchantResponse;
import com.linkvault.backend.merchant.model.Merchant;
import com.linkvault.backend.merchant.repository.MerchantRepository;
import com.linkvault.backend.security.CurrentUserService;
import com.linkvault.backend.user.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MerchantService {

    private final MerchantRepository repository;
    private final CurrentUserService currentUserService;
    private final GlobalMerchantRepository globalMerchantRepository;
    private final GlobalMerchantMatcher globalMerchantMatcher;

    public MerchantService(
            MerchantRepository repository,
            CurrentUserService currentUserService,
            GlobalMerchantRepository globalMerchantRepository,
            GlobalMerchantMatcher globalMerchantMatcher) {

        this.repository = repository;
        this.currentUserService = currentUserService;
        this.globalMerchantRepository = globalMerchantRepository;
        this.globalMerchantMatcher = globalMerchantMatcher;
    }

    public PageResponse<MerchantResponse> getMerchants(Pageable pageable) {

        User currentUser = currentUserService.getCurrentUser();

        Page<Merchant> page = repository.findByUserId(
                currentUser.getId(),
                pageable);

        return mapToPageResponse(page);
    }

    public MerchantResponse getMerchant(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Merchant merchant = repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Merchant Not Found"));

        return mapToResponse(merchant);
    }

    public MerchantResponse addMerchant(MerchantRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        repository.findByNameIgnoreCaseAndUserId(
                request.getName(),
                currentUser.getId())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Merchant with this name already exists");
                });

        Merchant merchant = new Merchant();

        merchant.setName(request.getName());
        merchant.setWebsiteUrl(request.getWebsiteUrl());
        merchant.setUser(currentUser);

        if (request.getGlobalMerchantId() != null) {

            GlobalMerchant globalMerchant = globalMerchantRepository
                    .findById(request.getGlobalMerchantId())
                    .orElseThrow(() -> new LinkNotFoundException(
                            "Global Merchant Not Found"));

            merchant.setGlobalMerchant(globalMerchant);

        } else {

            globalMerchantMatcher
                    .match(request.getName(), request.getWebsiteUrl())
                    .ifPresent(merchant::setGlobalMerchant);
        }

        Merchant savedMerchant = repository.save(merchant);

        return mapToResponse(savedMerchant);
    }

    public MerchantResponse updateMerchant(
            Long id,
            MerchantRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Merchant merchant = repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Merchant Not Found"));

        repository.findByNameIgnoreCaseAndUserId(
                request.getName(),
                currentUser.getId())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Merchant with this name already exists");
                });

        merchant.setName(request.getName());
        merchant.setWebsiteUrl(request.getWebsiteUrl());

        if (request.getGlobalMerchantId() != null) {

            GlobalMerchant globalMerchant = globalMerchantRepository
                    .findById(request.getGlobalMerchantId())
                    .orElseThrow(() -> new LinkNotFoundException(
                            "Global Merchant Not Found"));

            merchant.setGlobalMerchant(globalMerchant);

        } else {

            merchant.setGlobalMerchant(null);

            globalMerchantMatcher
                    .match(request.getName(), request.getWebsiteUrl())
                    .ifPresent(merchant::setGlobalMerchant);
        }

        Merchant updatedMerchant = repository.save(merchant);

        return mapToResponse(updatedMerchant);
    }

    public void deleteMerchant(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Merchant merchant = repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Merchant Not Found"));

        repository.delete(merchant);
    }

    private PageResponse<MerchantResponse> mapToPageResponse(
            Page<Merchant> page) {

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }

    private MerchantResponse mapToResponse(Merchant merchant) {

        return new MerchantResponse(
                merchant.getId(),
                merchant.getName(),
                merchant.getWebsiteUrl(),
                merchant.getGlobalMerchant() != null
                        ? merchant.getGlobalMerchant().getId()
                        : null,
                merchant.getGlobalMerchant() != null
                        ? merchant.getGlobalMerchant().getName()
                        : null);
    }
}