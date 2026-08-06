package com.linkvault.backend.service;

import com.linkvault.backend.model.Link;
import com.linkvault.backend.repository.LinkRepository;
import java.util.List;
import java.util.Optional;

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

    public List<Link> getAllLinks() {
        return repository.findAll();
    }

    public Link addLink(Link link) {
        return repository.save(link);
    }

    public boolean deleteLink(Long id) {

        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean updateLink(Long id, Link updatedLink) {

        return repository.findById(id)
                .map(link -> {
                    link.setTitle(updatedLink.getTitle());
                    link.setUrl(updatedLink.getUrl());
                    repository.save(link);
                    return true;
                })
                .orElse(false);
    }

    public Optional<Link> getLinkByTitle(String title) {
        return repository.findByTitle(title);
    }
}