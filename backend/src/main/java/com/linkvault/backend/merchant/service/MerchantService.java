package com.linkvault.backend.merchant.service;

import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.merchant.dto.MerchantRequest;
import com.linkvault.backend.merchant.model.Merchant;
import com.linkvault.backend.merchant.repository.MerchantRepository;
import com.linkvault.backend.security.CurrentUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.linkvault.backend.user.model.User;

@Service
public class MerchantService {

    private final MerchantRepository repository;
    private final CurrentUserService currentUserService;

    public MerchantService(MerchantRepository repository, CurrentUserService currentUserService) {
        this.repository = repository;
        this.currentUserService = currentUserService;
    }

    public PageResponse<Merchant> getMerchants(Pageable pageable) {

        User currentUser = currentUserService.getCurrentUser();

        Page<Merchant> page = repository.findByUserId(
                currentUser.getId(),
                pageable);

        return mapToPageResponse(page);
    }

    public Merchant getMerchant(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        return repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Merchant Not Found"));
    }

    public Merchant addMerchant(MerchantRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Merchant merchant = new Merchant();

        merchant.setName(request.getName());
        merchant.setWebsiteUrl(request.getWebsiteUrl());
        merchant.setUser(currentUser);

        return repository.save(merchant);
    }

    public Merchant updateMerchant(
            Long id,
            MerchantRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Merchant merchant = repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Merchant Not Found"));

        merchant.setName(request.getName());
        merchant.setWebsiteUrl(request.getWebsiteUrl());

        return repository.save(merchant);
    }

    public void deleteMerchant(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Merchant merchant = repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Merchant Not Found"));

        repository.delete(merchant);
    }

    private PageResponse<Merchant> mapToPageResponse(
            Page<Merchant> page) {

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
}