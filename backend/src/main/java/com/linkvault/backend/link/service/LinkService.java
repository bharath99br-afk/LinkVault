package com.linkvault.backend.link.service;

import com.linkvault.backend.common.dto.PageResponse;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.link.dto.LinkRequest;
import com.linkvault.backend.link.dto.LinkResponse;
import com.linkvault.backend.link.model.Link;
import com.linkvault.backend.link.repository.LinkRepository;

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

    public LinkService(LinkRepository repository, CurrentUserService currentUserService) {
        this.repository = repository;
        this.currentUserService = currentUserService;
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

    public LinkResponse updateLink(Long id, LinkRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Link link = repository.findByIdAndUserId(
                id,
                currentUser.getId())
                .orElseThrow(() -> new LinkNotFoundException("Link Not Found"));

        link.setTitle(request.getTitle());
        link.setUrl(request.getUrl());

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

        return new LinkResponse(
                link.getId(),
                link.getTitle(),
                link.getUrl());
    }
}