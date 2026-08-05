package com.linkvault.backend.service;

import com.linkvault.backend.model.Link;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class LinkService {

    private List<Link> links = new ArrayList<>();

    public LinkService() {
        links.add(new Link(1L, "abc", "www.ij.com"));
        links.add(new Link(2L, "dfg", "www.ss.com"));
        links.add(new Link(3L, "jue", "www.sd.com"));
        links.add(new Link(4L, "weewd", "www.sss.com"));
        links.add(new Link(5L, "cwwc", "www.yuuw.com"));
    }

    public List<Link> getAllLinks() {
        return links;
    }

    public void addLink(Link link) {
        links.add(link);
    }

    // public List<Link> getAllLinks() {
    // return List.of(
    // new Link(
    // 1L,
    // "Google",
    // "https://google.com"),
    // new Link(
    // 2L,
    // "OpenAI",
    // "https://openai.com"),

    // new Link(
    // 3L,
    // "Spring",
    // "https://spring.io"),
    // new Link(
    // 4L,
    // "YT",
    // "https://youTube.com"),
    // new Link(
    // 5L,
    // "Snap",
    // "https://SnapChat.io"));
    // }

    public Link getDemoLink() {
        return new Link(
                101L,
                "My First Micro SaaS",
                "https://bharat.dev");
    }

    public boolean deleteLink(Long id) {

        return links.removeIf(link -> link.getId().equals(id));
    }

    public boolean updateLink(Long id, Link updatedLink) {
        for (Link link : links) {
            if (link.getId().equals(id)) {

                link.setTitle(updatedLink.getTitle());
                link.setUrl(updatedLink.getUrl());

                return true;
            }
        }
        return false;
    }
}