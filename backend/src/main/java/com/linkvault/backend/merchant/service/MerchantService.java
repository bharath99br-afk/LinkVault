package com.linkvault.backend.merchant.service;

import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.merchant.dto.MerchantRequest;
import com.linkvault.backend.merchant.model.Merchant;
import com.linkvault.backend.merchant.repository.MerchantRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MerchantService {

    private final MerchantRepository repository;

    public MerchantService(MerchantRepository repository) {
        this.repository = repository;
    }

    public PageResponse<Merchant> getMerchants(Pageable pageable) {

        Page<Merchant> page = repository.findAll(pageable);

        return mapToPageResponse(page);
    }

    public Merchant getMerchant(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException("Merchant Not Found"));
    }

    public Merchant addMerchant(MerchantRequest request) {

        Merchant merchant = new Merchant();

        merchant.setName(request.getName());
        merchant.setWebsiteUrl(request.getWebsiteUrl());

        return repository.save(merchant);
    }

    public Merchant updateMerchant(
            Long id,
            MerchantRequest request) {

        Merchant merchant = repository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException("Merchant Not Found"));

        merchant.setName(request.getName());
        merchant.setWebsiteUrl(request.getWebsiteUrl());

        return repository.save(merchant);
    }

    public void deleteMerchant(Long id) {

        Merchant merchant = repository.findById(id)
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