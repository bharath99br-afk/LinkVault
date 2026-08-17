package com.linkvault.backend.service;

import com.linkvault.backend.dto.LinkRequest;
import com.linkvault.backend.dto.PageResponse;
import com.linkvault.backend.exception.LinkNotFoundException;
import com.linkvault.backend.model.Link;
import com.linkvault.backend.repository.LinkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

// public Link getDemoLink() {
//     return new Link(
//             101L,
//             "My First Micro SaaS",
//             "https://bharat.dev");
// }

@Service
public class LinkService {

    private final LinkRepository repository;

    public LinkService(LinkRepository repository) {
        this.repository = repository;
    }

    public PageResponse<Link> getAllLinks(Pageable pageable) {
        Page<Link> page = repository.findAll(pageable);

        return mapToPageResponse(page);
    }

    public Link addLink(LinkRequest request) {
        Link link = new Link();

        link.setTitle(request.getTitle());
        link.setUrl(request.getUrl());

        return repository.save(link);
    }

    public void deleteLink(Long id) {
        Link link = repository.findById(id).orElseThrow(() -> new LinkNotFoundException("Link Not Found"));
        repository.delete(link);
    }

    public Link updateLink(Long id, LinkRequest request) {

        return repository.findById(id)
                .map(link -> {
                    link.setTitle(request.getTitle());
                    link.setUrl(request.getUrl());
                    return repository.save(link);
                })
                .orElseThrow(() -> new LinkNotFoundException("Link Not Found"));
    }

    public PageResponse<Link> getLinkByTitle(String title, Pageable pageable) {
        Page<Link> page = repository.findByTitleContainingIgnoreCase(title, pageable);

        return mapToPageResponse(page);
    }

    public PageResponse<Link> getLinks(String title, Pageable pageable) {

        if (title == null || title.isBlank()) {
            return getAllLinks(pageable);
        }

        return getLinkByTitle(title, pageable);
    }

    private PageResponse<Link> mapToPageResponse(Page<Link> page) {

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