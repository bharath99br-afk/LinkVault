package com.linkvault.backend.globalmerchant.service;

import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.exception.DuplicateResourceException;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.globalmerchant.dto.GlobalMerchantRequest;
import com.linkvault.backend.globalmerchant.dto.GlobalMerchantResponse;
import com.linkvault.backend.globalmerchant.model.GlobalMerchant;
import com.linkvault.backend.globalmerchant.repository.GlobalMerchantRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GlobalMerchantService {

    private final GlobalMerchantRepository repository;

    public GlobalMerchantService(GlobalMerchantRepository repository) {
        this.repository = repository;
    }

    public PageResponse<GlobalMerchantResponse> getGlobalMerchants(
            String name,
            Pageable pageable) {

        Page<GlobalMerchant> page;

        if (name == null || name.isBlank()) {

            page = repository.findAll(pageable);

        } else {

            page = repository.findByNameContainingIgnoreCase(
                    name,
                    pageable);
        }

        return mapToPageResponse(page);
    }

    public GlobalMerchantResponse getGlobalMerchant(Long id) {

        GlobalMerchant globalMerchant = repository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException(
                        "Global Merchant Not Found"));

        return mapToResponse(globalMerchant);
    }

    public GlobalMerchantResponse addGlobalMerchant(
            GlobalMerchantRequest request) {

        repository.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Global Merchant with this name already exists");
                });

        GlobalMerchant globalMerchant = new GlobalMerchant();

        globalMerchant.setName(request.getName());
        globalMerchant.setWebsiteUrl(request.getWebsiteUrl());

        GlobalMerchant savedGlobalMerchant = repository.save(globalMerchant);

        return mapToResponse(savedGlobalMerchant);
    }

    public GlobalMerchantResponse updateGlobalMerchant(
            Long id,
            GlobalMerchantRequest request) {

        GlobalMerchant globalMerchant = repository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException(
                        "Global Merchant Not Found"));

        repository.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Global Merchant with this name already exists");
                });

        globalMerchant.setName(request.getName());
        globalMerchant.setWebsiteUrl(request.getWebsiteUrl());

        GlobalMerchant updatedGlobalMerchant = repository.save(globalMerchant);

        return mapToResponse(updatedGlobalMerchant);
    }

    public void deleteGlobalMerchant(Long id) {

        GlobalMerchant globalMerchant = repository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException(
                        "Global Merchant Not Found"));

        repository.delete(globalMerchant);
    }

    private PageResponse<GlobalMerchantResponse> mapToPageResponse(
            Page<GlobalMerchant> page) {

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

    private GlobalMerchantResponse mapToResponse(
            GlobalMerchant globalMerchant) {

        return new GlobalMerchantResponse(
                globalMerchant.getId(),
                globalMerchant.getName(),
                globalMerchant.getWebsiteUrl());
    }
}