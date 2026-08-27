package com.linkvault.backend.link.service;

import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.link.dto.LinkRequest;
import com.linkvault.backend.link.dto.LinkResponse;
import com.linkvault.backend.link.model.Link;
import com.linkvault.backend.link.repository.LinkRepository;
import com.linkvault.backend.merchant.model.Merchant;
import com.linkvault.backend.merchant.repository.MerchantRepository;
import com.linkvault.backend.product.model.Product;
import com.linkvault.backend.product.repository.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.linkvault.backend.security.CurrentUserService;
import com.linkvault.backend.user.model.User;

// public Link getDemoLink() {
//     return new Link(
//             101L,
//             "My First Micro SaaS",
//             "https://bharat.dev");
// }

@Service
public class LinkService {

    private final LinkRepository repository;
    private final CurrentUserService currentUserService;
    private final MerchantRepository merchantRepository;
    private final ProductRepository productRepository;

    public LinkService(LinkRepository repository, CurrentUserService currentUserService,
            MerchantRepository merchantRepository, ProductRepository productRepository) {
        this.repository = repository;
        this.currentUserService = currentUserService;
        this.merchantRepository = merchantRepository;
        this.productRepository = productRepository;
    }

    public PageResponse<LinkResponse> getAllLinks(Pageable pageable) {

        User currentUser = currentUserService.getCurrentUser();

        Page<Link> page = repository.findByUserId(currentUser.getId(), pageable);

        return mapToPageResponse(page);
    }

    public LinkResponse addLink(LinkRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Link link = new Link();

        link.setTitle(request.getTitle());
        link.setUrl(request.getUrl());
        link.setUser(currentUser);

        if (request.getMerchantId() != null) {

            Merchant merchant = merchantRepository
                    .findByIdAndUserId(
                            request.getMerchantId(),
                            currentUser.getId())
                    .orElseThrow(() -> new LinkNotFoundException("Merchant Not Found"));

            link.setMerchant(merchant);
        }

        if (request.getProductId() != null) {

            Product product = productRepository
                    .findByIdAndUserId(
                            request.getProductId(),
                            currentUser.getId())
                    .orElseThrow(() -> new LinkNotFoundException("Product Not Found"));

            link.setProduct(product);
        }

        Link savedLink = repository.save(link);

        return mapToResponse(savedLink);
    }

    public void deleteLink(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Link link = repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Link Not Found"));

        repository.delete(link);
    }

    public LinkResponse updateLink(
            Long id,
            LinkRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Link link = repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Link Not Found"));

        link.setTitle(request.getTitle());
        link.setUrl(request.getUrl());

        if (request.getMerchantId() != null) {

            Merchant merchant = merchantRepository
                    .findByIdAndUserId(
                            request.getMerchantId(),
                            currentUser.getId())
                    .orElseThrow(() -> new LinkNotFoundException("Merchant Not Found"));

            link.setMerchant(merchant);

        } else {

            link.setMerchant(null);
        }
        if (request.getProductId() != null) {

            Product product = productRepository
                    .findByIdAndUserId(
                            request.getProductId(),
                            currentUser.getId())
                    .orElseThrow(() -> new LinkNotFoundException("Product Not Found"));

            link.setProduct(product);

        } else {

            link.setProduct(null);
        }

        Link updatedLink = repository.save(link);
        return mapToResponse(updatedLink);
    }

    public PageResponse<LinkResponse> getLinkByTitle(
            String title,
            Pageable pageable) {

        User currentUser = currentUserService.getCurrentUser();

        Page<Link> page = repository.findByUserIdAndTitleContainingIgnoreCase(
                currentUser.getId(),
                title,
                pageable);

        return mapToPageResponse(page);
    }

    public PageResponse<LinkResponse> getLinks(String title, Pageable pageable) {

        if (title == null || title.isBlank()) {
            return getAllLinks(pageable);
        }

        return getLinkByTitle(title, pageable);
    }

    private PageResponse<LinkResponse> mapToPageResponse(Page<Link> page) {

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

    private LinkResponse mapToResponse(Link link) {

        Merchant merchant = link.getMerchant();
        Product product = link.getProduct();

        return new LinkResponse(
                link.getId(),
                link.getTitle(),
                link.getUrl(),
                merchant != null ? merchant.getId() : null,
                merchant != null ? merchant.getName() : null,
                product != null ? product.getId() : null,
                product != null ? product.getName() : null);
    }
}