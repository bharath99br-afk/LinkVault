package com.linkvault.backend.product.service;

import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.globalmerchant.model.GlobalMerchant;
import com.linkvault.backend.merchant.model.Merchant;
import com.linkvault.backend.merchant.repository.MerchantRepository;
import com.linkvault.backend.product.dto.ProductRequest;
import com.linkvault.backend.product.dto.ProductResponse;
import com.linkvault.backend.product.model.Product;
import com.linkvault.backend.product.repository.ProductRepository;
import com.linkvault.backend.security.CurrentUserService;
import com.linkvault.backend.user.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final CurrentUserService currentUserService;
    private final MerchantRepository merchantRepository;

    public ProductService(
            ProductRepository repository,
            CurrentUserService currentUserService,
            MerchantRepository merchantRepository) {

        this.repository = repository;
        this.currentUserService = currentUserService;
        this.merchantRepository = merchantRepository;
    }

    public PageResponse<ProductResponse> getProducts(
            String name,
            String category,
            Pageable pageable) {

        User currentUser = currentUserService.getCurrentUser();

        Page<Product> page;

        boolean hasName = name != null && !name.isBlank();
        boolean hasCategory = category != null && !category.isBlank();

        if (!hasName && !hasCategory) {

            page = repository.findByUserId(
                    currentUser.getId(),
                    pageable);

        } else if (hasName && !hasCategory) {

            page = repository.findByUserIdAndNameContainingIgnoreCase(
                    currentUser.getId(),
                    name,
                    pageable);

        } else if (!hasName && hasCategory) {

            page = repository.findByUserIdAndCategoryContainingIgnoreCase(
                    currentUser.getId(),
                    category,
                    pageable);

        } else {

            page = repository
                    .findByUserIdAndNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(
                            currentUser.getId(),
                            name,
                            category,
                            pageable);
        }

        return mapToPageResponse(page);
    }

    public ProductResponse getProduct(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Product product = repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Product Not Found"));

        return mapToResponse(product);
    }

    public ProductResponse addProduct(ProductRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(request.getCategory());
        product.setWebsiteUrl(request.getWebsiteUrl());
        product.setUser(currentUser);

        Merchant merchant = resolveMerchant(
                request.getMerchantId(),
                currentUser.getId());

        product.setMerchant(merchant);

        Product savedProduct = repository.save(product);

        return mapToResponse(savedProduct);
    }

    public ProductResponse updateProduct(
            Long id,
            ProductRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Product product = repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Product Not Found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(request.getCategory());
        product.setWebsiteUrl(request.getWebsiteUrl());

        Merchant merchant = resolveMerchant(
                request.getMerchantId(),
                currentUser.getId());

        product.setMerchant(merchant);

        Product updatedProduct = repository.save(product);

        return mapToResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Product product = repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Product Not Found"));

        repository.delete(product);
    }

    private Merchant resolveMerchant(
            Long merchantId,
            Long userId) {

        if (merchantId == null) {
            return null;
        }

        return merchantRepository.findByIdAndUserId(
                merchantId,
                userId)
                .orElseThrow(() -> new LinkNotFoundException("Merchant Not Found"));
    }

    private PageResponse<ProductResponse> mapToPageResponse(
            Page<Product> page) {

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

    private ProductResponse mapToResponse(Product product) {

        Merchant merchant = product.getMerchant();

        GlobalMerchant globalMerchant = merchant != null ? merchant.getGlobalMerchant() : null;

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl(),
                product.getCategory(),
                product.getWebsiteUrl(),

                merchant != null ? merchant.getId() : null,
                merchant != null ? merchant.getName() : null,

                globalMerchant != null ? globalMerchant.getId() : null,
                globalMerchant != null ? globalMerchant.getName() : null);
    }
}